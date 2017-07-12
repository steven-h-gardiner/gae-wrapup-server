package com.google.appengine.api.labs.datastore.overlay;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreAttributes;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Index;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * A simple wrapper class that combines an {@link AsyncDatastoreService} with a {@link Transaction}.
 * The purpose of this class is to avoid code duplication between the transaction and
 * non-transaction versions of several API methods.
 */
final class TransactionLinkedAsyncDatastoreServiceImpl implements AsyncDatastoreService {

  private final AsyncDatastoreService datastore;
  private final Transaction txn;

  public TransactionLinkedAsyncDatastoreServiceImpl(AsyncDatastoreService datastore, Transaction txn) {
    this.datastore = datastore;
    this.txn = txn;
  }

  @Override
  public Future<Entity> get(Key key) {
    checkNotNull(key);
    return datastore.get(txn, key);
  }

  @Override
  public Future<Entity> get( Transaction txn, Key key) {
    throw new UnsupportedOperationException(
        "if you want to pass a txn explicitly, don't use this class");
  }

  @Override
  public Future<Map<Key, Entity>> get(Iterable<Key> keys) {
    checkNotNull(keys);
    return datastore.get(txn, keys);
  }

  @Override
  public Future<Map<Key, Entity>> get( Transaction txn, Iterable<Key> keys) {
    throw new UnsupportedOperationException(
        "if you want to pass a txn explicitly, don't use this class");
  }

  @Override
  public Future<Key> put(Entity entity) {
    checkNotNull(entity);
    return datastore.put(txn, entity);
  }

  @Override
  public Future<Key> put( Transaction txn, Entity entity) {
    throw new UnsupportedOperationException(
        "if you want to pass a txn explicitly, don't use this class");
  }

  @Override
  public Future<List<Key>> put(Iterable<Entity> entities) {
    checkNotNull(entities);
    return datastore.put(txn, entities);
  }

  @Override
  public Future<List<Key>> put( Transaction txn, Iterable<Entity> entities) {
    throw new UnsupportedOperationException(
        "if you want to pass a txn explicitly, don't use this class");
  }

  @Override
  public Future<Void> delete(Key... keys) {
    return datastore.delete(txn, keys);
  }

  @Override
  public Future<Void> delete( Transaction txn, Key... keys) {
    throw new UnsupportedOperationException(
        "if you want to pass a txn explicitly, don't use this class");
  }

  @Override
  public Future<Void> delete(Iterable<Key> keys) {
    checkNotNull(keys);
    return datastore.delete(txn, keys);
  }

  @Override
  public Future<Void> delete( Transaction txn, Iterable<Key> keys) {
    throw new UnsupportedOperationException(
        "if you want to pass a txn explicitly, don't use this class");
  }

  @Override
  public Future<Transaction> beginTransaction() {
    return datastore.beginTransaction();
  }

  @Override
  public Future<Transaction> beginTransaction(TransactionOptions options) {
    checkNotNull(options);
    return datastore.beginTransaction(options);
  }

  @Override
  public Future<KeyRange> allocateIds(String kind, long num) {
    checkNotNull(kind);
    return datastore.allocateIds(kind, num);
  }

  @Override
  public Future<KeyRange> allocateIds(Key parent, String kind, long num) {
    checkNotNull(parent);
    checkNotNull(kind);
    return datastore.allocateIds(parent, kind, num);
  }

  @Override
  public Future<DatastoreAttributes> getDatastoreAttributes() {
    return datastore.getDatastoreAttributes();
  }

  @Override
  public Future<Map<Index, Index.IndexState>> getIndexes() {
    return datastore.getIndexes();
  }

  @Override
  public PreparedQuery prepare(Query query) {
    checkNotNull(query);
    return datastore.prepare(txn, query);
  }

  @Override
  public PreparedQuery prepare(Transaction txn, Query query) {
    throw new UnsupportedOperationException(
        "if you want to pass a txn explicitly, don't use this class");
  }

  @Override
  public Transaction getCurrentTransaction() {
    return datastore.getCurrentTransaction();
  }

  @Override
  public Transaction getCurrentTransaction(Transaction returnedIfNoTxn) {
    return datastore.getCurrentTransaction(returnedIfNoTxn);
  }

  @Override
  public Collection<Transaction> getActiveTransactions() {
    return datastore.getActiveTransactions();
  }
}
