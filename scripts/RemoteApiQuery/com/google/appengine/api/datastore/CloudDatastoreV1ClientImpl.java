package com.google.appengine.api.datastore;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.datastore.v1.AllocateIdsRequest;
import com.google.datastore.v1.AllocateIdsResponse;
import com.google.datastore.v1.BeginTransactionRequest;
import com.google.datastore.v1.BeginTransactionResponse;
import com.google.datastore.v1.CommitRequest;
import com.google.datastore.v1.CommitResponse;
import com.google.datastore.v1.LookupRequest;
import com.google.datastore.v1.LookupResponse;
import com.google.datastore.v1.RollbackRequest;
import com.google.datastore.v1.RollbackResponse;
import com.google.datastore.v1.RunQueryRequest;
import com.google.datastore.v1.RunQueryResponse;
import com.google.datastore.v1.client.Datastore;
import com.google.datastore.v1.client.DatastoreException;
import com.google.datastore.v1.client.DatastoreFactory;
import com.google.datastore.v1.client.DatastoreOptions;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A thread-safe {@link CloudDatastoreV1Client} that makes remote proto-over-HTTP calls. */
final class CloudDatastoreV1ClientImpl implements CloudDatastoreV1Client {

  private static final Logger logger = Logger.getLogger(CloudDatastoreV1ClientImpl.class.getName());

  private static final ExecutorService executor = Executors.newCachedThreadPool();

  private static Datastore DATASTORE_INSTANCE = null;

  final Datastore datastore;

  CloudDatastoreV1ClientImpl(Datastore datastore) {
    this.datastore = checkNotNull(datastore);
  }

  /** Creates a {@link CloudDatastoreV1ClientImpl}. */
  static synchronized CloudDatastoreV1ClientImpl create() {
    if (DATASTORE_INSTANCE == null) {
      Preconditions.checkState(!DatastoreServiceGlobalConfig.getConfig().useApiProxy());
      String projectId =
          DatastoreApiHelper.toProjectId(
              DatastoreServiceGlobalConfig.getConfig().configuredAppId());
      DatastoreOptions options;
      try {
        options = createDatastoreOptions(projectId);
      } catch (GeneralSecurityException | IOException e) {
        throw new RuntimeException("Could not get Cloud Datastore options from environment.", e);
      }
      DATASTORE_INSTANCE = DatastoreFactory.get().create(options);
    }
    return new CloudDatastoreV1ClientImpl(DATASTORE_INSTANCE);
  }

  @Override
  public Future<BeginTransactionResponse> beginTransaction(final BeginTransactionRequest req) {
    return makeCall(
        new Callable<BeginTransactionResponse>() {
          @Override
          public BeginTransactionResponse call() throws DatastoreException {
            return datastore.beginTransaction(req);
          }
        });
  }

  @Override
  public Future<RollbackResponse> rollback(final RollbackRequest req) {
    return makeCall(
        new Callable<RollbackResponse>() {
          @Override
          public RollbackResponse call() throws DatastoreException {
            return datastore.rollback(req);
          }
        });
  }

  @Override
  public Future<RunQueryResponse> runQuery(final RunQueryRequest req) {
    return makeCall(
        new Callable<RunQueryResponse>() {
          @Override
          public RunQueryResponse call() throws DatastoreException {
            return datastore.runQuery(req);
          }
        });
  }

  @Override
  public Future<LookupResponse> lookup(final LookupRequest req) {
    return makeCall(
        new Callable<LookupResponse>() {
          @Override
          public LookupResponse call() throws DatastoreException {
            return datastore.lookup(req);
          }
        });
  }

  @Override
  public Future<AllocateIdsResponse> allocateIds(final AllocateIdsRequest req) {
    return makeCall(
        new Callable<AllocateIdsResponse>() {
          @Override
          public AllocateIdsResponse call() throws DatastoreException {
            return datastore.allocateIds(req);
          }
        });
  }

  private Future<CommitResponse> commit(final CommitRequest req) {
    return makeCall(
        new Callable<CommitResponse>() {
          @Override
          public CommitResponse call() throws DatastoreException {
            return datastore.commit(req);
          }
        });
  }

  @Override
  public Future<CommitResponse> rawCommit(byte[] bytes) {
    try {
      return commit(CommitRequest.parseFrom(bytes));
    } catch (InvalidProtocolBufferException e) {
      throw new IllegalStateException(e);
    }
  }

  private static <T extends Message> Future<T> makeCall(final Callable<T> request) {
    final Exception stackTraceCapturer = new Exception();
    return executor.submit(
        new Callable<T>() {
          @Override
          public T call() throws Exception {
            try {
              return request.call();
            } catch (DatastoreException e) {
              String message =
                  String.format(
                      "%s%nstack trace when async call was initiated: <%n%s>",
                      e.getMessage(), Throwables.getStackTraceAsString(stackTraceCapturer));
              throw DatastoreApiHelper.createV1Exception(e.getCode(), message, e);
            }
          }
        });
  }

  private static DatastoreOptions createDatastoreOptions(String projectId)
      throws GeneralSecurityException, IOException {
    DatastoreOptions.Builder options = new DatastoreOptions.Builder();
    setProjectEndpoint(projectId, options);
    options.credential(getCredential());
    return options.build();
  }

  private static Credential getCredential() throws GeneralSecurityException, IOException {
    if (DatastoreServiceGlobalConfig.getConfig().emulatorHost() != null) {
      logger.log(Level.INFO, "Emulator host was provided. Not using credentials.");
      return null;
    }
    String serviceAccount = DatastoreServiceGlobalConfig.getConfig().serviceAccount();
    String privateKeyFile = DatastoreServiceGlobalConfig.getConfig().privateKeyFile();
    if (serviceAccount != null) {
      Preconditions.checkState(privateKeyFile != null);
      logger.log(
          Level.INFO,
          "Service account and private key file were provided. "
              + "Using service account credential.");
      return getServiceAccountCredential(serviceAccount, privateKeyFile);
    }
    return GoogleCredential.getApplicationDefault()
        .createScoped(DatastoreOptions.SCOPES);
  }

  private static void setProjectEndpoint(String projectId, DatastoreOptions.Builder options) {
    if (DatastoreServiceGlobalConfig.getConfig().hostOverride() != null) {
      options.projectEndpoint(
          String.format(
              "%s/%s/projects/%s",
              DatastoreServiceGlobalConfig.getConfig().hostOverride(),
              DatastoreFactory.VERSION.toLowerCase(),
              projectId));
      return;
    }
    if (DatastoreServiceGlobalConfig.getConfig().emulatorHost() != null) {
      options.projectId(projectId);
      options.localHost(DatastoreServiceGlobalConfig.getConfig().emulatorHost());
      return;
    }
    options.projectId(projectId);
    return;
  }

  private static Credential getServiceAccountCredential(String account, String privateKeyFile)
      throws GeneralSecurityException, IOException {
    return new GoogleCredential.Builder()
        .setTransport(GoogleNetHttpTransport.newTrustedTransport())
        .setJsonFactory(new JacksonFactory())
        .setServiceAccountId(account)
        .setServiceAccountScopes(DatastoreOptions.SCOPES)
        .setServiceAccountPrivateKeyFromP12File(new File(privateKeyFile))
        .build();
  }
}
