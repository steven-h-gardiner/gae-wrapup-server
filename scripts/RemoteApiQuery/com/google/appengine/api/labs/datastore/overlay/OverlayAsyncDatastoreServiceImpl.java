package com.google.appengine.api.labs.datastore.overlay;

import static com.google.appengine.api.datastore.FutureHelper.quietGet;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreAttributes;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Index;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * An implementation of {@link AsyncDatastoreService} using an overlay model. Conceptually, an
 * overlay Datastore is based on some other Datastore (the "parent"). The overlay allows developers
 * to effectively update or delete entities on the parent, but without actually modifying the data
 * that the parent stores. (There is one exception: ID allocation is forwarded to the parent
 * Datastore.)
 */
final class OverlayAsyncDatastoreServiceImpl implements OverlayAsyncDatastoreService {
  private final String name;
  private final AsyncDatastoreService datastore;
  private final AsyncDatastoreService parent;

  OverlayAsyncDatastoreServiceImpl(String name, AsyncDatastoreService datastore,
      AsyncDatastoreService parent) {
    this.name = checkNotNull(name);
    this.datastore = checkNotNull(datastore);
    this.parent = checkNotNull(parent);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public AsyncDatastoreService getParentDatastoreService() {
    return parent;
  }

  @Override
  public Future<Entity> get(Key key) {
    checkNotNull(key);
    return getImpl(datastore, key);
  }

  @Override
  public Future<Entity> get( Transaction txn, Key key) {
    checkNotNull(key);
    return getImpl(getTxnAsyncDatastore(txn), key);
  }

  private Future<Entity> getImpl(AsyncDatastoreService datastore, final Key key) {
    checkNotNull(datastore);
    checkNotNull(key);
    return new RethrowingFutureWrapper<Map<Key, Entity>, Entity>(
        getImpl(datastore, ImmutableList.of(key))) {
      @Override
      protected Entity wrap(Map<Key, Entity> entityMap) throws Exception {
        Entity entity = entityMap.get(key);
        if (entity == null) {
          throw new EntityNotFoundException(key);
        }
        return entity;
      }
    };
  }

  @Override
  public Future<Map<Key, Entity>> get(Iterable<Key> keys) {
    checkNotNull(keys);
    return getImpl(datastore, keys);
  }

  @Override
  public Future<Map<Key, Entity>> get( Transaction txn, Iterable<Key> keys) {
    checkNotNull(keys);
    return getImpl(getTxnAsyncDatastore(txn), keys);
  }

  private Future<Map<Key, Entity>> getImpl(final AsyncDatastoreService datastore,
      final Iterable<Key> keys) {
    checkNotNull(datastore);
    checkNotNull(keys);

    return new RethrowingFutureWrapper<Map<Key, Entity>, Map<Key, Entity>>(
        datastore.get(OverlayUtils.getKeysAndTombstoneKeys(keys))) {
      @Override
      protected Map<Key, Entity> wrap(Map<Key, Entity> results) throws Exception {
        Set<Key> remainingKeys = Sets.newHashSet(keys);
        Iterator<Map.Entry<Key, Entity>> entryIterator = results.entrySet().iterator();
        while (entryIterator.hasNext()) {
          Map.Entry<Key, Entity> entry = entryIterator.next();
          if (OverlayUtils.isTombstone(entry.getValue())) {
            entryIterator.remove();
            remainingKeys.remove(OverlayUtils.getKeyFromTombstoneKey(entry.getKey()));
          } else {
            remainingKeys.remove(entry.getKey());
          }
        }

        results.putAll(quietGet(parent.get(null, remainingKeys)));

        return results;
      }
    };
  }

  @Override
  public Future<Key> put(Entity entity) {
    checkNotNull(entity);
    return putImpl(datastore, entity);
  }

  @Override
  public Future<Key> put( Transaction txn, Entity entity) {
    checkNotNull(entity);
    return putImpl(getTxnAsyncDatastore(txn), entity);
  }

  private Future<Key> putImpl(AsyncDatastoreService datastore, Entity entity) {
    checkNotNull(datastore);
    checkNotNull(entity);
    Future<List<Key>> futureKeys = putImpl(datastore, ImmutableList.of(entity));
    return new RethrowingFutureWrapper<List<Key>, Key>(futureKeys) {
      @Override
      protected Key wrap(List<Key> keys) throws Exception {
        return keys.get(0);
      }
    };
  }

  @Override
  public Future<List<Key>> put(Iterable<Entity> entities) {
    checkNotNull(entities);
    return putImpl(datastore, entities);
  }

  @Override
  public Future<List<Key>> put( Transaction txn, Iterable<Entity> entities) {
    checkNotNull(entities);
    return putImpl(getTxnAsyncDatastore(txn), entities);
  }

  private Future<List<Key>> putImpl(final AsyncDatastoreService datastore,
      final Iterable<Entity> entities) {
    checkNotNull(datastore);
    checkNotNull(entities);

    return new FakeFutureTask<List<Key>>() {
      @Override
      public List<Key> call() throws Exception {
        Future<Void> futureDelete = datastore.delete(OverlayUtils.getTombstoneKeysForEntities(
            OverlayUtils.getEntitiesWithPreexistingCompleteKeys(entities)));

        Map<IncompleteKey, Integer> idsNeededMap = OverlayUtils.getIdsNeededMap(entities);

        Map<IncompleteKey, Future<KeyRange>> idsAllocatedFutureMap = Maps.newHashMap();
        for (Map.Entry<IncompleteKey, Integer> entry : idsNeededMap.entrySet()) {
          IncompleteKey incompleteKey = entry.getKey();
          idsAllocatedFutureMap.put(incompleteKey,
              allocateIds(incompleteKey.parent, incompleteKey.kind, entry.getValue()));
        }

        Map<IncompleteKey, Iterator<Key>> idsAllocatedMap = Maps.newHashMap();
        for (Map.Entry<IncompleteKey, Future<KeyRange>> entry : idsAllocatedFutureMap.entrySet()) {
          idsAllocatedMap.put(entry.getKey(), quietGet(entry.getValue()).iterator());
        }

        Future<List<Key>> futureKeys =
            datastore.put(OverlayUtils.translateAndCompleteEntityKeys(entities, idsAllocatedMap));

        quietGet(futureDelete);
        return quietGet(futureKeys);
      }
    };
  }

  @Override
  public Future<Void> delete(Key... keys) {
    checkNotNull(keys);
    return delete(ImmutableList.copyOf(keys));
  }

  @Override
  public Future<Void> delete( Transaction txn, Key... keys) {
    checkNotNull(keys);
    return delete(txn, ImmutableList.copyOf(keys));
  }

  @Override
  public Future<Void> delete(Iterable<Key> keys) {
    checkNotNull(keys);
    return deleteImpl(datastore, keys);
  }

  @Override
  public Future<Void> delete( Transaction txn, Iterable<Key> keys) {
    checkNotNull(keys);
    return deleteImpl(getTxnAsyncDatastore(txn), keys);
  }

  private Future<Void> deleteImpl(final AsyncDatastoreService datastore, final Iterable<Key> keys) {
    checkNotNull(datastore);
    checkNotNull(keys);

    Future<List<Key>> futurePut = datastore.put(OverlayUtils.getTombstonesForKeys(keys));

    final Future<Void> futureDelete = datastore.delete(keys);

    return new RethrowingFutureWrapper<List<Key>, Void>(futurePut) {
      @Override
      protected Void wrap(List<Key> tombstoneKeys) throws Exception {
        return quietGet(futureDelete);
      }
    };
  }

  @Override
  public Future<Transaction> beginTransaction() {
    return datastore.beginTransaction();
  }

  @Override
  public Future<Transaction> beginTransaction(TransactionOptions options) {
    return datastore.beginTransaction(options);
  }

  @Override
  public Future<KeyRange> allocateIds(String kind, long num) {
    checkNotNull(kind);
    return parent.allocateIds(kind, num);
  }

  @Override
  public Future<KeyRange> allocateIds(Key parent, String kind, long num) {
    checkNotNull(kind);
    return this.parent.allocateIds(parent, kind, num);
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
    return prepare(null, query);
  }

  @Override
  public PreparedQuery prepare(Transaction txn, Query query) {
    PreparedQuery preparedOverlayQuery = datastore.prepare(txn, query);
    PreparedQuery preparedParentQuery = getParentDatastoreService().prepare(null, query);
    return new OverlayPreparedQueryImpl(this, query, preparedOverlayQuery, preparedParentQuery,
        txn);
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

  /**
   * Retrieve entities directly from the overlay, without pulling rows through from the base.
   */
  Map<Key, Entity> getFromOverlayOnly( Transaction txn, Iterable<Key> keys) {
    checkNotNull(keys);
    try {
      return datastore.get(txn, keys).get();
    } catch (Exception e) {
      return Maps.newHashMap();
    }
  }

  /**
   * Gets a version of the Datastore that is linked to {@code txn}.
   */
  private AsyncDatastoreService getTxnAsyncDatastore( Transaction txn) {
    return new TransactionLinkedAsyncDatastoreServiceImpl(datastore, txn);
  }
}
