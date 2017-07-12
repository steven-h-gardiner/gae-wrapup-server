// Copyright 2008 Google Inc. All Rights Reserved.
package com.google.appengine.api.datastore;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityService.ParsedAppId;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.FutureWrapper;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.ApiConfig;
import com.google.apphosting.datastore.DatastoreV3Pb.DatastoreService_3;
import com.google.apphosting.datastore.DatastoreV3Pb.Error;
import com.google.io.protocol.ProtocolMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.Code;
import java.util.ConcurrentModificationException;
import java.util.concurrent.Future;

/**
 * Helper methods and constants shared by classes that implement the Java api
 * on top of the datastore.
 *
 * <p>Note: users should not access this class directly.
 *
 */
public final class DatastoreApiHelper {

  static final String DATASTORE_V3_PACKAGE = "datastore_v3";
  static final String CLOUD_DATASTORE_V1_PACKAGE = "cloud_datastore_v1";

  /**
   * Key to put in {@link ApiProxy.Environment#getAttributes()} to override the app id used by the
   * datastore api.  If absent, {@link ApiProxy.Environment#getAppId()} will be used.
   */
  @SuppressWarnings("javadoc")
  static final String APP_ID_OVERRIDE_KEY = "com.google.appengine.datastore.AppIdOverride";

  private static final AppIdentityService appIdentityService =
      AppIdentityServiceFactory.getAppIdentityService();

  private DatastoreApiHelper() {}

  public static RuntimeException translateError(ApiProxy.ApplicationException exception) {
    Error.ErrorCode errorCode = Error.ErrorCode.valueOf(exception.getApplicationError());
    if (errorCode == null) {
      return new DatastoreFailureException(exception.getErrorDetail());
    }
    switch (errorCode) {
      case BAD_REQUEST:
        return new IllegalArgumentException(exception.getErrorDetail());

      case CONCURRENT_TRANSACTION:
        return new ConcurrentModificationException(exception.getErrorDetail());

      case NEED_INDEX:
        return new DatastoreNeedIndexException(exception.getErrorDetail());

      case TIMEOUT:
      case BIGTABLE_ERROR:
        return new DatastoreTimeoutException(exception.getErrorDetail());

      case COMMITTED_BUT_STILL_APPLYING:
        return new CommittedButStillApplyingException(exception.getErrorDetail());

      case INTERNAL_ERROR:
      default:
        return new DatastoreFailureException(exception.getErrorDetail());
    }
  }

  static RuntimeException createV1Exception(Code code, String message, Throwable cause) {
    if (code == null) {
      return new DatastoreFailureException(message, cause);
    }
    switch (code) {
      case ABORTED:
        return new ConcurrentModificationException(message, cause);
      case FAILED_PRECONDITION:
        if (message.contains("The Cloud Datastore API is not enabled for the project")) {
          return new DatastoreFailureException(message, cause);
        }
        return new DatastoreNeedIndexException(message, cause);
      case DEADLINE_EXCEEDED:
        return new DatastoreTimeoutException(message, cause);
      case INVALID_ARGUMENT:
      case PERMISSION_DENIED:
        return new IllegalArgumentException(message, cause);
      case UNAVAILABLE:
        return new ApiProxy.RPCFailedException(message, cause);
      case RESOURCE_EXHAUSTED:
        return new ApiProxy.OverQuotaException(message, cause);
      case INTERNAL:
      default:
        return new DatastoreFailureException(message, cause);
    }
  }

  static <T extends ProtocolMessage<T>> Future<T> makeAsyncCall(ApiConfig apiConfig,
      final DatastoreService_3.Method method, ProtocolMessage<?> request, final T responseProto) {
    Future<byte[]> response = ApiProxy.makeAsyncCall(DATASTORE_V3_PACKAGE, method.name(),
        request.toByteArray(), apiConfig);
    return new FutureWrapper<byte[], T>(response) {
      @Override
      protected T wrap(byte[] responseBytes) throws InvalidProtocolBufferException {
        if (responseBytes != null) {
          if (!responseProto.parseFrom(responseBytes)) {
            throw new InvalidProtocolBufferException(String.format("Invalid %s.%s response",
                    DATASTORE_V3_PACKAGE, method.name()));
          }
          String initializationError = responseProto.findInitializationError();
          if (initializationError != null) {
            throw new InvalidProtocolBufferException(initializationError);
          }
        }
        return responseProto;
      }

      @Override
      protected Throwable convertException(Throwable cause) {
        if (cause instanceof ApiProxy.ApplicationException) {
          return translateError((ApiProxy.ApplicationException) cause);
        }
        return cause;
      }
    };
  }

  static String getCurrentProjectId() {
    return toProjectId(getCurrentAppId());
  }

  static String toProjectId(String appId) {
    ParsedAppId parsedAppId = appIdentityService.parseFullAppId(appId);
    if (parsedAppId.getDomain().isEmpty()) {
      return parsedAppId.getId();
    } else {
      return String.format("%s:%s", parsedAppId.getDomain(), parsedAppId.getId());
    }
  }

  static String getCurrentAppId() {
    ApiProxy.Environment environment = DatastoreServiceGlobalConfig.getCurrentApiProxyEnvironment();
    if (environment == null) {
      throw new NullPointerException("No API environment is registered for this thread.");
    }

    Object appIdOverride = environment.getAttributes().get(APP_ID_OVERRIDE_KEY);
    if (appIdOverride != null) {
      return (String) appIdOverride;
    }

    return environment.getAppId();
  }

  /**
   * Returns a new {@link AppIdNamespace} with the current appId and the namespace
   * registered with the {@link NamespaceManager}
   */
  static AppIdNamespace getCurrentAppIdNamespace() {
    return getCurrentAppIdNamespace(getCurrentAppId());
  }

  /**
   * Returns a new {@link AppIdNamespace} with the namespace currently
   * registered with the {@link NamespaceManager} for a given appid.
   */
  static AppIdNamespace getCurrentAppIdNamespace(String appId) {
    String namespace = NamespaceManager.get();
    namespace = namespace == null ? "" : namespace;
    return new AppIdNamespace(appId, namespace);
  }
}
