package com.google.appengine.api.datastore;

import com.google.appengine.api.utils.FutureWrapper;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.primitives.Bytes;
import com.google.datastore.v1.BeginTransactionResponse;
import com.google.datastore.v1.CommitRequest;
import com.google.datastore.v1.Mutation;
import com.google.datastore.v1.RollbackRequest;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of the Cloud Datastore v1 specific logic to handle a {@link Transaction}.
 *
 * <p>In Cloud Datastore, puts and gets are stored on the client until commit. This class serializes
 * mutations as they are received to avoid memory penalties associated with the full
 * proto objects.
 */
class InternalTransactionCloudDatastoreV1 implements TransactionImpl.InternalTransaction {
  /**
   * Prefix for transaction IDs ({@link #getId()}). Enables this class to reliably distinguish
   * transactions it created from v3 transactions, whose IDs are always entirely numerical.
   */
  private static final String TXN_ID_PREFIX = "v1-";

  private final CommitRequest.Builder commitReqBuilder = CommitRequest.newBuilder();

  /**
   * Generates a unique identifier (for a given runtime) which can be used for later
   * lookup of the instance.
   */
  private static final AtomicLong clientIdGenerator = new AtomicLong();

  /**
   * Used to store {@link InternalTransactionCloudDatastoreV1} objects for reidentification when a
   * potentially wrapped Transaction object is passed back to the SDK in a future call.
   * Each {@link InternalTransactionCloudDatastoreV1} instance is wrapped in a
   * {@link TransactionImpl}. We use weak references in this static map because this object's
   * purpose is tied to the lifetime of the wrapper.
   */
  private static final Map<String, InternalTransactionCloudDatastoreV1>
  internalTransactionRegister = new MapMaker().weakValues().makeMap();

  /**
   * The ID reported through {@link #getId()}. This ID is also used for instance lookup, see
   * {@link #get(Transaction)}.
   */
  private final String clientId = TXN_ID_PREFIX
      + Long.toString(clientIdGenerator.getAndIncrement());

  /**
   * The list of mutations (deferred Put/Delete operations) that will be sent to the server as part
   * of the Commit RPC. A linked map is used to generate consistent results for unit tests; however
   * iteration order shouldn't affect correctness.
   */
  private final Map<com.google.datastore.v1.Key, byte[]> mutationMap = Maps.newLinkedHashMap();

  /**
   * The {@link Future} associated with the BeginTransaction RPC we sent to the
   * datastore server.
   */
  private final Future<BeginTransactionResponse> beginTxnFuture;

  protected final CloudDatastoreV1Client dsApiProxy;

  private boolean isWritable = true;

  /**
   * Objects should be created with {@link #create(CloudDatastoreV1Client, Future)} due to
   * post-construction manipulation.
   */
  private InternalTransactionCloudDatastoreV1(
      CloudDatastoreV1Client dsApiProxy, Future<BeginTransactionResponse> beginTxnFuture) {
    this.dsApiProxy = dsApiProxy;
    this.beginTxnFuture = beginTxnFuture;
  }

  static TransactionImpl.InternalTransaction create(
      CloudDatastoreV1Client dsApiProxy, Future<BeginTransactionResponse> future) {
    return registerTxn(new InternalTransactionCloudDatastoreV1(dsApiProxy, future));
  }

  /**
   * Convert a mutation to a format suitable for committing later.
   */
  byte[] serializeMutation(Mutation mutation) {
    byte[] bytes = commitReqBuilder.addMutations(mutation).buildPartial().toByteArray();
    commitReqBuilder.clearMutations();
    return bytes;
  }

  /**
   * Convert the partial proto segments into a serialized {@link CommitRequest}.
   */
  Future<?> sendCommit(Collection<byte[]> mutations) {
    byte[][] protoSegmentsArray = new byte[mutations.size() + 1][];
    protoSegmentsArray[0] = CommitRequest.newBuilder()
        .setTransaction(getTransactionBytes())
        .build().toByteArray();
    int arrayIndex = 1;
    for (byte[] mutData : mutations) {
      protoSegmentsArray[arrayIndex++] = mutData;
    }
    try {
      return dsApiProxy.rawCommit(Bytes.concat(protoSegmentsArray));
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException("Unexpected error.", e);
    }
  }

  /**
   * Register a new transaction on the internal roaster.
   * @return The txn, for chaining.
   */
  static InternalTransactionCloudDatastoreV1 registerTxn(InternalTransactionCloudDatastoreV1 txn) {
    internalTransactionRegister.put(txn.clientId, txn);
    return txn;
  }

  /**
   * Returns the transaction bytes for this transaction.
   * Blocks on the future since the bytes are returned by the datastore server.
   */
  ByteString getTransactionBytes() {
    return FutureHelper.quietGet(beginTxnFuture).getTransaction();
  }

  /**
   * Schedules a put operation for when this transaction is committed.
   */
  void deferPut(Entity entity) {
    deferPut(DataTypeTranslator.toV1Entity(entity));
  }

  void deferPut(com.google.datastore.v1.Entity.Builder entityProto) {
    checkWritable();
    mutationMap.put(entityProto.getKey(), serializeMutation(Mutation.newBuilder()
        .setUpsert(entityProto)
        .build()));
  }

  void deferDelete(Key key) {
    checkWritable();
    com.google.datastore.v1.Key keyV1 = DataTypeTranslator.toV1Key(key).build();
    mutationMap.put(keyV1, serializeMutation(Mutation.newBuilder().setDelete(keyV1).build()));
  }

  @Override
  public Future<Void> doCommitAsync() {
    isWritable = false;
    Future<Void> result = new VoidFutureWrapper<>(sendCommit(mutationMap.values()));
    mutationMap.clear();
    return result;
  }

  @Override
  public Future<Void> doRollbackAsync() {
    isWritable = false;
    mutationMap.clear();
    return new VoidFutureWrapper<>(dsApiProxy.rollback(
        RollbackRequest.newBuilder().setTransaction(getTransactionBytes()).build()));
  }

  @Override
  public String getId() {
    return clientId;
  }

  private void checkWritable() {
    if (!isWritable) {
      throw new IllegalStateException("Transaction is not writable.");
    }
  }

  /**
   * Locates the {@link InternalTransactionCloudDatastoreV1} object associated with a
   * {@link Transaction} by looking up the ID in an static, threadsafe map.
   *
   * @throws IllegalArgumentException If a txn object is not found.
   * @return Internal transaction object associated with the given ID.
   */
  static InternalTransactionCloudDatastoreV1 get(Transaction txn) {
    String txnId = txn.getId();
    InternalTransactionCloudDatastoreV1 txnImpl = internalTransactionRegister.get(txnId);
    if (txnImpl == null) {
      throw new IllegalArgumentException("Transaction not found with ID: " + txnId);
    }
    return txnImpl;
  }

  static boolean isV1Transaction(Transaction txn) {
    return internalTransactionRegister.containsKey(txn.getId());
  }

  private static class VoidFutureWrapper<T> extends FutureWrapper<T, Void> {
    public VoidFutureWrapper(Future<T> parent) {
      super(parent);
    }

    @Override
    protected Void wrap(T ignore) throws Exception {
      return null;
    }

    @Override
    protected Throwable convertException(Throwable cause) {
      return cause;
    }
  }
}
