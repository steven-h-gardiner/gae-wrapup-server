package com.google.appengine.api.datastore;

import static com.google.common.base.Preconditions.checkState;
import static com.google.datastore.v1.client.DatastoreHelper.LOCAL_HOST_ENV_VAR;
import static com.google.datastore.v1.client.DatastoreHelper.PRIVATE_KEY_FILE_ENV_VAR;
import static com.google.datastore.v1.client.DatastoreHelper.PROJECT_ID_ENV_VAR;
import static com.google.datastore.v1.client.DatastoreHelper.SERVICE_ACCOUNT_ENV_VAR;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.utils.SystemProperty.Environment.Value;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.api.ApiProxy.EnvironmentFactory;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.Nullable;

/** See {@link CloudDatastoreRemoteServiceConfig}. */
@AutoValue
abstract class DatastoreServiceGlobalConfig {

  private static final Logger logger =
      Logger.getLogger(DatastoreServiceGlobalConfig.class.getName());

  static final String ADDITIONAL_APP_IDS_VAR = "DATASTORE_ADDITIONAL_APP_IDS";
  static final String USE_PROJECT_ID_AS_APP_ID_VAR = "DATASTORE_USE_PROJECT_ID_AS_APP_ID";
  static final String APP_ID_VAR = "DATASTORE_APP_ID";

  private static final Splitter COMMA_SPLITTER = Splitter.on(',');

  private static DatastoreServiceGlobalConfig config;

  static synchronized DatastoreServiceGlobalConfig getConfig() {
    if (config == null) {
      setConfig(DatastoreServiceGlobalConfig.fromEnv());
    }
    return config;
  }

  static synchronized void setConfig(DatastoreServiceGlobalConfig config) {
    Preconditions.checkState(DatastoreServiceGlobalConfig.config == null);

    if (!config.useApiProxy()) {
      if (SystemProperty.environment.value() == Value.Development) {
        logger.warning(
            "Using non-API proxy mode in the development server. "
                + "This mode will not work on production App Engine Standard.");
      } else if (SystemProperty.environment.value() == Value.Production) {
        if (EnvProxy.getenv("GAE_VM") == null && EnvProxy.getenv("GCLOUD_PROJECT") == null) {
          throw new IllegalStateException(
              "Cannot use non-API proxy mode on production App Engine Standard.");
        } else {
          logger.info("Allowing non-API proxy mode on production App Engine Flex.");
        }
      }
    }

    DatastoreServiceGlobalConfig.config = config;
    maybeSetUpApiProxyEnvironment();
  }

  static synchronized void clearConfig() {
    config = null;
  }

  static synchronized void clear() {
    clearConfig();
    try {
      maybeClearApiProxyEnvironment();
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  static void maybeClearApiProxyEnvironment() throws ReflectiveOperationException {
    Field field = ApiProxy.class.getDeclaredField("environmentFactory");
    field.setAccessible(true);
    Object environmentFactory = field.get(null);
    if (!(environmentFactory instanceof StubApiProxyEnvironmentFactory)) {
      return;
    }
    field.set(null, null);
    ApiProxy.clearEnvironmentForCurrentThread();
  }

  private static synchronized void maybeSetUpApiProxyEnvironment() {
    if (getConfig().useApiProxy()
        || ApiProxy.getCurrentEnvironment() != null
        || !getConfig().installApiProxyEnvironment()) {
      return;
    }

    ApiProxy.setEnvironmentFactory(
        new StubApiProxyEnvironmentFactory(getConfig().configuredAppId()));

    ImmutableSet<String> additionalAppIds =
        DatastoreServiceGlobalConfig.getConfig().additionalAppIds();
    if (additionalAppIds != null) {
      ImmutableMap.Builder<String, String> projectIdToAppId = ImmutableMap.builder();
      for (String appId : additionalAppIds) {
        projectIdToAppId.put(DatastoreApiHelper.toProjectId(appId), appId.toString());
      }
      ApiProxy.getCurrentEnvironment()
          .getAttributes()
          .put(DataTypeTranslator.ADDITIONAL_APP_IDS_MAP_ATTRIBUTE_KEY, projectIdToAppId.build());
    }
  }

  /**
   * Returns the current {@link Environment}.
   *
   * <p>If no {@link Environment} has been configured, installs a stubbed-out version and returns
   * it.
   */
  static Environment getCurrentApiProxyEnvironment() {
    maybeSetUpApiProxyEnvironment();
    return ApiProxy.getCurrentEnvironment();
  }

  abstract boolean useApiProxy();

  @Nullable
  abstract String hostOverride();

  @Nullable
  abstract ImmutableSet<String> additionalAppIds();

  abstract boolean installApiProxyEnvironment();

  @Nullable
  abstract String appId();

  @Nullable
  abstract String projectId();

  abstract boolean useProjectIdAsAppId();

  @Nullable
  abstract String emulatorHost();

  @Nullable
  abstract String serviceAccount();

  @Nullable
  abstract String privateKeyFile();

  /**
   * Returns the app ID that should be used in actual API objects. Could be an app ID or a project
   * ID depending on how the user has configured things.
   */
  String configuredAppId() {
    if (appId() != null) {
      return appId().toString();
    }
    checkState(useProjectIdAsAppId());
    return projectId();
  }

  /** Returns a {@link DatastoreServiceGlobalConfig.Builder}. */
  static DatastoreServiceGlobalConfig.Builder builder() {
    return new AutoValue_DatastoreServiceGlobalConfig.Builder()
        .useApiProxy(false)
        .useProjectIdAsAppId(false)
        .installApiProxyEnvironment(true);
  }

  /** Builder for {@link DatastoreServiceGlobalConfig}. */
  @AutoValue.Builder
  abstract static class Builder {

    abstract DatastoreServiceGlobalConfig.Builder appId(String value);

    abstract DatastoreServiceGlobalConfig.Builder emulatorHost(String value);

    abstract DatastoreServiceGlobalConfig.Builder hostOverride(String value);

    abstract DatastoreServiceGlobalConfig.Builder additionalAppIds(Set<String> value);

    abstract DatastoreServiceGlobalConfig.Builder installApiProxyEnvironment(boolean value);

    abstract DatastoreServiceGlobalConfig.Builder useApiProxy(boolean value);

    abstract DatastoreServiceGlobalConfig.Builder serviceAccount(String value);

    abstract DatastoreServiceGlobalConfig.Builder privateKeyFile(String value);

    abstract DatastoreServiceGlobalConfig.Builder projectId(String value);

    abstract DatastoreServiceGlobalConfig.Builder useProjectIdAsAppId(boolean value);

    abstract DatastoreServiceGlobalConfig autoBuild();

    /**
     * Build a {@link DatastoreServiceGlobalConfig} instance.
     *
     * @throws IllegalStateException if the {@link DatastoreServiceGlobalConfig} instance is
     *     invalid.
     */
    DatastoreServiceGlobalConfig build() {
      DatastoreServiceGlobalConfig config = autoBuild();

      if (config.useApiProxy()) {
        checkState(config.appId() == null);
        checkState(config.emulatorHost() == null);
        checkState(config.hostOverride() == null);
        checkState(config.additionalAppIds() == null);

        checkState(config.serviceAccount() == null);
        checkState(config.privateKeyFile() == null);
        checkState(config.projectId() == null);
        checkState(!config.useProjectIdAsAppId());
        return config;
      }

      checkState(
          config.appId() == null || config.projectId() == null,
          "Cannot provide both app ID and project ID.");
      if (config.appId() != null) {
        checkState(
            !config.useProjectIdAsAppId(),
            "Cannot use project ID as app ID if app ID was provided.");
      } else if (config.projectId() != null) {
        checkState(
            config.useProjectIdAsAppId(),
            "Must use project ID as app ID if project ID is provided.");
      }

      if (config.emulatorHost() != null) {
        checkState(
            config.hostOverride() == null, "Cannot provide both host override and emulator host.");
        if (config.serviceAccount() != null || config.privateKeyFile() != null) {
          logger.warning(
              "Emulator host was provided, so service account and private key "
                  + "file will not be used.");
        }
      }
      checkState(
          (config.serviceAccount() == null) == (config.privateKeyFile() == null),
          "Service account must be provided if and only if private key file is provided.");

      return config;
    }
  }

  /** Creates an {@link DatastoreServiceGlobalConfig} objects from environment variables. */
  private static DatastoreServiceGlobalConfig fromEnv() {
    DatastoreServiceGlobalConfig.Builder builder = DatastoreServiceGlobalConfig.builder();

    boolean useCloudDatastoreApi =
        Boolean.valueOf(EnvProxy.getenv("DATASTORE_USE_CLOUD_DATASTORE"))
            || EnvProxy.getenv(APP_ID_VAR) != null
            || EnvProxy.getenv(PROJECT_ID_ENV_VAR) != null;
    builder.useApiProxy(!useCloudDatastoreApi);

    if (EnvProxy.getenv(ADDITIONAL_APP_IDS_VAR) != null) {
      Set<String> additionalAppIds = new HashSet<>();
      for (String appId : COMMA_SPLITTER.split(EnvProxy.getenv(ADDITIONAL_APP_IDS_VAR))) {
        appId = appId.trim();
        if (!appId.isEmpty()) {
          additionalAppIds.add(appId);
        }
      }
      builder.additionalAppIds(additionalAppIds);
    }
    if (EnvProxy.getenv(APP_ID_VAR) != null) {
      builder.appId(EnvProxy.getenv(APP_ID_VAR));
    }
    if (EnvProxy.getenv(PROJECT_ID_ENV_VAR) != null) {
      builder.projectId(EnvProxy.getenv(PROJECT_ID_ENV_VAR));
    }
    builder.useProjectIdAsAppId(Boolean.valueOf(EnvProxy.getenv(USE_PROJECT_ID_AS_APP_ID_VAR)));
    if (EnvProxy.getenv(LOCAL_HOST_ENV_VAR) != null) {
      builder.emulatorHost(EnvProxy.getenv(LOCAL_HOST_ENV_VAR));
    }
    if (EnvProxy.getenv(SERVICE_ACCOUNT_ENV_VAR) != null) {
      builder.serviceAccount(EnvProxy.getenv(SERVICE_ACCOUNT_ENV_VAR));
    }
    if (EnvProxy.getenv(PRIVATE_KEY_FILE_ENV_VAR) != null) {
      builder.privateKeyFile(EnvProxy.getenv(PRIVATE_KEY_FILE_ENV_VAR));
    }

    return builder.build();
  }

  /** An {@link EnvironmentFactory} that builds {@link StubApiProxyEnvironment}s. */
  static class StubApiProxyEnvironmentFactory implements EnvironmentFactory {
    private final String appId;

    public StubApiProxyEnvironmentFactory(String appId) {
      this.appId = appId;
    }

    @Override
    public Environment newEnvironment() {
      return new StubApiProxyEnvironment(appId);
    }
  }

  /**
   * An {@link Environment} that supports the minimal subset of features needed to run code from the
   * datastore package outside of App Engine. All other methods throw {@link
   * UnsupportedOperationException}.
   */
  static class StubApiProxyEnvironment implements Environment {
    private final Map<String, Object> attributes;
    private final String appId;

    public StubApiProxyEnvironment(String appId) {
      this.attributes = new HashMap<>();
      this.appId = appId;
    }

    @Override
    public boolean isLoggedIn() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAdmin() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getVersionId() {
      throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public String getRequestNamespace() {
      throw new UnsupportedOperationException();
    }

    @Override
    public long getRemainingMillis() {
      return 1L;
    }

    @Override
    public String getModuleId() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getEmail() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthDomain() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getAttributes() {
      return attributes;
    }

    @Override
    public String getAppId() {
      return appId;
    }
  }
}
