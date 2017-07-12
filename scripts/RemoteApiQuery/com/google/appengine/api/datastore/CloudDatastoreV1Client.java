package com.google.appengine.api.datastore;

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
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.concurrent.Future;

/**
 * The Cloud Datastore v1 RPC interface.
 *
 * <p>Invoking a method sends out the supplied RPC and returns a {@link Future} which clients can
 * block on to retrieve a result.
 */
interface CloudDatastoreV1Client {
  Future<BeginTransactionResponse> beginTransaction(BeginTransactionRequest request);
  Future<RollbackResponse> rollback(RollbackRequest request);
  Future<RunQueryResponse> runQuery(RunQueryRequest request);
  Future<LookupResponse> lookup(LookupRequest request);
  Future<AllocateIdsResponse> allocateIds(AllocateIdsRequest request);

  /**
   * Calls commit with a pre-serialized {@link CommitRequest} proto. Used by {@link
   * InternalTransactionCloudDatastoreV1} to avoid a second serialization of the proto.
   *
   * @param request byte array which must be deserializable as a {@link CommitRequest}.
   */
  Future<CommitResponse> rawCommit(byte[] request) throws InvalidProtocolBufferException;
}
