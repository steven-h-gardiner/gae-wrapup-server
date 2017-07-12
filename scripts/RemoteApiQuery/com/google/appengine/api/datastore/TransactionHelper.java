package com.google.appengine.api.datastore;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.appengine.api.taskqueue.TaskQueuePb.TaskQueueAddRequest;
import com.google.apphosting.api.AppEngineInternal;

/**
 * {@link TransactionHelper} enables the task queue API to serialize a datastore transaction
 * without knowing the details of how it is implemented.
 */
@AppEngineInternal
public final class TransactionHelper {

  private TransactionHelper() {}

  /**
   * Sets either the transaction or datastore_transaction field in a TaskQueueAddRequest depending
   * on what kind of transaction is provided.
   */
  public static void setTransaction(Transaction txn, TaskQueueAddRequest request) {
    checkNotNull(txn);
    checkNotNull(request);

    if (InternalTransactionCloudDatastoreV1.isV1Transaction(txn)) {
      request.setDatastoreTransactionAsBytes(
          InternalTransactionCloudDatastoreV1.get(txn).getTransactionBytes().toByteArray());
    } else {
      request.setTransaction(InternalTransactionV3.toProto(txn));
    }
  }
}
