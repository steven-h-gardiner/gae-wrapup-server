package com.google.appengine.api.labs.datastore.overlay;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreAttributes;
import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Index;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * An implementation of {@link DatastoreService} in terms of {@link AsyncDatastoreService}.
 */
class SyncDatastoreServiceAdapter extends DelegatedBaseDatastoreService implements
    DatastoreService {
  final AsyncDatastoreService datastore;

  public SyncDatastoreServiceAdapter(AsyncDatastoreService datastore) {
    super(datastore);
    this.datastore = checkNotNull(datastore);
  }

  @Override
  public Entity get(Key key) throws EntityNotFoundException {
    return getAndConvertExceptionsExceptEntityNotFound(datastore.get(key));
  }

  @Override
  public Entity get(Transaction txn, Key key) throws EntityNotFoundException {
    return getAndConvertExceptionsExceptEntityNotFound(datastore.get(txn, key));
  }

  @Override
  public Map<Key, Entity> get(Iterable<Key> keys) {
    return getAndConvertExceptions(datastore.get(keys));
  }

  @Override
  public Map<Key, Entity> get(Transaction txn, Iterable<Key> keys) {
    return getAndConvertExceptions(datastore.get(txn, keys));
  }

  @Override
  public Key put(Entity entity) {
    return getAndConvertExceptions(datastore.put(entity));
  }

  @Override
  public Key put(Transaction txn, Entity entity) {
    return getAndConvertExceptions(datastore.put(txn, entity));
  }

  @Override
  public List<Key> put(Iterable<Entity> entities) {
    return getAndConvertExceptions(datastore.put(entities));
  }

  @Override
  public List<Key> put(Transaction txn, Iterable<Entity> entities) {
    return getAndConvertExceptions(datastore.put(txn, entities));
  }

  @Override
  public void delete(Key... keys) {
    getAndConvertExceptions(datastore.delete(keys));
  }

  @Override
  public void delete(Transaction txn, Key... keys) {
    getAndConvertExceptions(datastore.delete(txn, keys));
  }

  @Override
  public void delete(Iterable<Key> keys) {
    getAndConvertExceptions(datastore.delete(keys));
  }

  @Override
  public void delete(Transaction txn, Iterable<Key> keys) {
    getAndConvertExceptions(datastore.delete(txn, keys));
  }

  @Override
  public Transaction beginTransaction() {
    return getAndConvertExceptions(datastore.beginTransaction());
  }

  @Override
  public Transaction beginTransaction(TransactionOptions options) {
    return getAndConvertExceptions(datastore.beginTransaction(options));
  }

  @Override
  public KeyRange allocateIds(String kind, long num) {
    return getAndConvertExceptions(datastore.allocateIds(kind, num));
  }

  @Override
  public KeyRange allocateIds(Key parent, String kind, long num) {
    return getAndConvertExceptions(datastore.allocateIds(parent, kind, num));
  }

  @Override
  public KeyRangeState allocateIdRange(KeyRange range) {
    throw new UnsupportedOperationException("allocateIdRange not supported on async Datastores");
  }

  @Override
  public DatastoreAttributes getDatastoreAttributes() {
    return getAndConvertExceptions(datastore.getDatastoreAttributes());
  }

  @Override
  public Map<Index, Index.IndexState> getIndexes() {
    return getAndConvertExceptions(datastore.getIndexes());
  }

  private <T> T getAndConvertExceptions(Future<T> future) {
    try {
      return future.get();
    } catch (Exception e) {
      throw new DatastoreFailureException("Datastore failure", e);
    }
  }

  private <T> T getAndConvertExceptionsExceptEntityNotFound(Future<T> future)
      throws EntityNotFoundException {
    try {
      return future.get();
    } catch (ExecutionException e) {
      if (e.getCause() instanceof EntityNotFoundException) {
        throw ((EntityNotFoundException) e.getCause());
      } else {
        throw new DatastoreFailureException("Datastore failure", e);
      }
    } catch (Exception e) {
      throw new DatastoreFailureException("Datastore failure", e);
    }
  }
}
