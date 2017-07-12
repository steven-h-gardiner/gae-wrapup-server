package com.google.appengine.api.datastore;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.api.ApiProxy.EnvironmentFactory;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * User-configurable global properties of Cloud Datastore.
 *
 * <p>Code running in other environments can use the Cloud Datastore API by making a single call to
 * {@link #setConfig} before accessing any other classes from {@link com.google.appengine.api}. For
 * example:
 *
 * <pre>
 * public static void main(Strings[] args) {
 *   CloudDatastoreRemoteServiceConfig config = CloudDatastoreRemoteServiceConfig.builder()
 *       .appId(AppId.create(Location.US_CENTRAL, "my-project-id"))
 *       .build();
 *   CloudDatastoreRemoteServiceConfig.setConfig(config);
 *   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
 *   ...
 * }
 * </pre>
 *
 * Outside of tests, the config should not be cleared once it has been set. In tests, the config can
 * be cleared by calling {@link #clear}:
 *
 * <pre>
 * {@literal @}Before
 * public void before() {
 *   CloudDatastoreRemoteServiceConfig config = CloudDatastoreRemoteServiceConfig.builder()
 *       .appId(AppId.create(Location.US_CENTRAL, "my-project-id"))
 *       .emulatorHost(...)
 *       .build();
 *   CloudDatastoreRemoteServiceConfig.setConfig(config);
 * }
 *
 * {@literal @}After
 * public void after() {
 *   CloudDatastoreRemoteServiceConfig.clear();
 * }
 * </pre>
 */
@AutoValue
abstract class CloudDatastoreRemoteServiceConfig {

  /**
   * Sets the {@link CloudDatastoreRemoteServiceConfig} instance.
   *
   * @throws IllegalStateException if the {@link CloudDatastoreRemoteServiceConfig} instance has
   *     already been set and {@link #clear} has not been called
   * @throws IllegalStateException if the provided {@link CloudDatastoreRemoteServiceConfig} is not
   *     supported in this environment
   */
  public static void setConfig(CloudDatastoreRemoteServiceConfig config) {
    DatastoreServiceGlobalConfig.setConfig(config.toInternalConfig());
  }

  /**
   * Clears the {@link CloudDatastoreRemoteServiceConfig} instance (if one has been set) as well as
   * the {@link ApiProxy}'s {@link EnvironmentFactory} and the {@link Environment} for the current
   * thread.
   *
   * <p>This method should only be called in tests.
   */
  public static void clear() {
    DatastoreServiceGlobalConfig.clear();
  }

  /** Converts this to a {@link DatastoreServiceGlobalConfig}. */
  private DatastoreServiceGlobalConfig toInternalConfig() {
    return DatastoreServiceGlobalConfig.builder()
        .appId(appId().toString())
        .emulatorHost(emulatorHost())
        .hostOverride(hostOverride())
        .additionalAppIds(additionalAppIdsAsStrings())
        .serviceAccount(serviceAccount())
        .privateKeyFile(privateKeyFile())
        .build();
  }

  /** An App Engine application ID. */
  @AutoValue
  public abstract static class AppId {
    /** Locations for App Engine applications. */
    public static enum Location {
      US_CENTRAL("s"),
      EUROPE_WEST("e"),
      US_EAST1("p"),
      ASIA_NORTHEAST1("b");

      private static final ImmutableMap<String, Location> byId;

      static {
        ImmutableMap.Builder<String, Location> builder = ImmutableMap.builder();
        for (Location location : Location.values()) {
          builder.put(location.id(), location);
        }
        byId = builder.build();
      }

      private final String id;

      private Location(String id) {
        this.id = id;
      }

      String id() {
        return id;
      }

      @Nullable
      static Location forId(String id) {
        return byId.get(id);
      }

      /**
       * Returns the {@link Location} for a location string. The location string is case-insensitive
       * and may use hyphens to separate components. For example, given the location string {@code
       * us-central}, this method returns {@link #US_CENTRAL}.
       *
       * @throws IllegalArgumentException if {@code locationString} does not correspond to a known
       *     {@link Location}
       * @throws NullPointerException if {@code locationString} is null
       */
      public static Location fromString(String locationString) {
        return valueOf(locationString.toUpperCase().replaceAll("-", "_"));
      }
    }

    abstract Location location();

    abstract String projectId();

    /**
     * Creates an {@link AppId}.
     *
     * @param location The location of the App Engine application. This can be found in the Google
     *     Cloud Console.
     * @param projectId The project ID of the App Engine application. This can be found in the
     *     Google Cloud Console.
     * @throws NullPointerException if {@code location} or {@code projectId} is null
     */
    public static AppId create(Location location, String projectId) {
      return new AutoValue_CloudDatastoreRemoteServiceConfig_AppId(location, projectId);
    }

    @Override
    public String toString() {
      return String.format("%s~%s", location().id(), projectId());
    }

    /**
     * Create an {@link AppId} from an app ID string.
     *
     * @throws IllegalArgumentException if {@code appIdString} cannot be parsed or it does not
     *     correspond to a known {@link Location}
     */
    public static AppId fromString(String appIdString) {
      String[] parts = appIdString.split("~");
      if (parts.length != 2) {
        throw new IllegalArgumentException("Invalid app ID string: " + appIdString);
      }
      Location location = Location.forId(parts[0]);
      if (location == null) {
        throw new IllegalArgumentException("Unknown location: " + parts[0]);
      }
      return create(location, parts[1]);
    }
  }

  @Nullable
  abstract AppId appId();

  @Nullable
  abstract String emulatorHost();

  @Nullable
  abstract String hostOverride();

  @Nullable
  abstract ImmutableSet<AppId> additionalAppIds();

  abstract boolean installApiProxyEnvironment();

  @Nullable
  abstract String serviceAccount();

  @Nullable
  abstract String privateKeyFile();

  @Nullable
  ImmutableSet<String> additionalAppIdsAsStrings() {
    if (additionalAppIds() == null) {
      return null;
    }
    ImmutableSet.Builder<String> appIds = ImmutableSet.builder();
    for (AppId appId : additionalAppIds()) {
      appIds.add(appId.toString());
    }
    return appIds.build();
  }

  /** Returns a {@link CloudDatastoreRemoteServiceConfig.Builder}. */
  public static CloudDatastoreRemoteServiceConfig.Builder builder() {
    return new AutoValue_CloudDatastoreRemoteServiceConfig.Builder()
        .installApiProxyEnvironment(true);
  }

  /** Builder for {@link CloudDatastoreRemoteServiceConfig}. */
  @AutoValue.Builder
  public abstract static class Builder {
    /** Sets the {@link AppId} of the Cloud Datastore instance to call. Required. */
    public abstract CloudDatastoreRemoteServiceConfig.Builder appId(AppId value);

    /**
     * Instructs the client to connect to a locally-running Cloud Datastore Emulator and not to pass
     * credentials.
     */
    public abstract CloudDatastoreRemoteServiceConfig.Builder emulatorHost(String value);

    /**
     * Overrides the host (e.g. {@code datastore.googleapis.com}) used to contact the Cloud
     * Datastore API. To connect to the Cloud Datastore Emulator, use {@link #emulatorHost} instead.
     */
    public abstract CloudDatastoreRemoteServiceConfig.Builder hostOverride(String value);

    /**
     * Provides a set of additional app IDs that may appear in {@link Key} values in entities.
     *
     * <p>This is only required if the client will read entities containing {@link Key} values that
     * contain app IDs other than the one provided to {@link #appId}. Any such app IDs should be
     * provided to this method.
     */
    public abstract CloudDatastoreRemoteServiceConfig.Builder additionalAppIds(Set<AppId> value);

    /**
     * If set to true, a minimal {@link Environment} will be installed (if none is already
     * installed).
     *
     * <p>If set to false, no attempt to install an environment will be made and the user must
     * install it instead. At a minimum, such an environment must provide implementations for {@link
     * Environment#getAppId()}, {@link Environment#getAttributes()}, and {@link
     * Environment#getRemainingMillis()}.
     */
    public abstract CloudDatastoreRemoteServiceConfig.Builder installApiProxyEnvironment(
        boolean value);

    abstract CloudDatastoreRemoteServiceConfig.Builder serviceAccount(String value);

    abstract CloudDatastoreRemoteServiceConfig.Builder privateKeyFile(String value);

    abstract CloudDatastoreRemoteServiceConfig autoBuild();

    public CloudDatastoreRemoteServiceConfig build() {
      CloudDatastoreRemoteServiceConfig config = autoBuild();
      config.toInternalConfig();
      return config;
    }
  }
}
