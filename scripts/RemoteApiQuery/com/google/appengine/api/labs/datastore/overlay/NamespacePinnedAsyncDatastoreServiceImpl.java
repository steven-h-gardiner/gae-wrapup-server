package com.google.appengine.api.labs.datastore.overlay;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.BaseDatastoreService;
import com.google.appengine.api.datastore.DatastoreAttributes;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Index;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Projection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * A thin wrapper around {@link AsyncDatastoreService}, where all operations are redirected to use
 * an alternate namespace.
 *
 * <p>For operations which use the ambient namespace from the {@link NamespaceManager} (such as
 * {@code allocateIds}), {@code namespacePrefix} is applied to the current namespace
 * before completing the operation.
 *
 * <p>For operations whose arguments contain namespaces in some form (such as keys),
 * {@code namespacePrefix} is applied to the relevant namespaces before completing the
 * operation.
 *
 * <p>In all cases, there are no lasting effects on the ambient namespace; it is restored to the
 * original value after the operation is done.
 */
final class NamespacePinnedAsyncDatastoreServiceImpl implements AsyncDatastoreService {
  private final AsyncDatastoreService datastore;
  private final String namespacePrefix;
  private final boolean shouldReserveIds;

  /**
   * Constructs a new {@link NamespacePinnedAsyncDatastoreServiceImpl}.
   *
   * @param datastore the underlying {@link AsyncDatastoreService}
   * @param namespacePrefix the prefix to apply to all namespaces
   * @param shouldReserveIds a flag that indicates whether calling {@code put()} should explicitly
   *        reserve (not allocate) the numeric IDs used in any complete keys before storing them
   */
  NamespacePinnedAsyncDatastoreServiceImpl(AsyncDatastoreService datastore, String namespacePrefix,
      boolean shouldReserveIds) {
    this.datastore = checkNotNull(datastore);
    this.namespacePrefix = checkNotNull(namespacePrefix);
    this.shouldReserveIds = shouldReserveIds;
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

  private Future<Entity> getImpl(AsyncDatastoreService datastore, Key key) {
    checkNotNull(datastore);
    checkNotNull(key);
    return new RethrowingFutureWrapper<Entity, Entity>(
        datastore.get(getAlternateNamespaceKey(key))) {
      @Override
      protected Entity wrap(Entity entity) throws Exception {
        return getOriginalNamespaceEntity(entity);
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

  private Future<Map<Key, Entity>> getImpl(AsyncDatastoreService datastore, Iterable<Key> keys) {
    checkNotNull(datastore);
    checkNotNull(keys);
    return new RethrowingFutureWrapper<Map<Key, Entity>, Map<Key, Entity>>(
        datastore.get(getAlternateNamespaceKeys(keys))) {
      @Override
      protected Map<Key, Entity> wrap(Map<Key, Entity> entityMap) throws Exception {
        return getOriginalNamespaceEntityMap(entityMap);
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

  private Future<Key> putImpl(final AsyncDatastoreService datastore, Entity entity) {
    checkNotNull(datastore);
    checkNotNull(entity);
    final Entity alternateEntity = getAlternateNamespaceEntity(entity);
    return new RethrowingFutureWrapper<Key, Key>(datastore.put(alternateEntity)) {
      @Override
      protected Key wrap(Key key) throws Exception {
        return getOriginalNamespaceKey(key);
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
      Iterable<Entity> entities) {
    checkNotNull(datastore);
    checkNotNull(entities);
    final List<Entity> alternateEntities = getAlternateNamespaceEntities(entities);
    return new RethrowingFutureWrapper<List<Key>, List<Key>>(datastore.put(alternateEntities)) {
      @Override
      protected List<Key> wrap(List<Key> keys) throws Exception {
        return getOriginalNamespaceKeys(keys);
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
    return datastore.delete(getAlternateNamespaceKeys(keys));
  }

  @Override
  public Future<Void> delete( Transaction txn, Iterable<Key> keys) {
    checkNotNull(keys);
    return datastore.delete(txn, getAlternateNamespaceKeys(keys));
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
    String currentNamespace = NamespaceManager.get();
    try {
      NamespaceManager.set(getAlternateNamespace(currentNamespace));
      return new RethrowingFutureWrapper<KeyRange, KeyRange>(datastore.allocateIds(kind, num)) {
        @Override
        protected KeyRange wrap(KeyRange range) throws Exception {
          return getOriginalNamespaceKeyRange(range);
        }
      };
    } finally {
      NamespaceManager.set(currentNamespace);
    }
  }

  @Override
  public Future<KeyRange> allocateIds(Key parent, String kind, long num) {
    checkNotNull(parent);
    checkNotNull(kind);
    String currentNamespace = NamespaceManager.get();
    try {
      NamespaceManager.set(getAlternateNamespace(parent.getNamespace()));
      return new RethrowingFutureWrapper<KeyRange, KeyRange>(
          datastore.allocateIds(getAlternateNamespaceKey(parent), kind, num)) {
        @Override
        protected KeyRange wrap(KeyRange range) throws Exception {
          return getOriginalNamespaceKeyRange(range);
        }
      };
    } finally {
      NamespaceManager.set(currentNamespace);
    }
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
  public Collection<Transaction> getActiveTransactions() {
    return datastore.getActiveTransactions();
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
  public PreparedQuery prepare(Query query) {
    checkNotNull(query);
    return prepareImpl(datastore, query);
  }

  @Override
  public PreparedQuery prepare(Transaction txn, Query query) {
    checkNotNull(query);
    return prepareImpl(getTxnAsyncDatastore(txn), query);
  }

  private PreparedQuery prepareImpl(BaseDatastoreService datastore, Query query) {
    checkNotNull(datastore);
    checkNotNull(query);
    Query alternateQuery = getAlternateNamespaceQuery(query);
    return new NamespacePinnedPreparedQueryImpl(this, datastore.prepare(alternateQuery));
  }

  /**
   * Returns {@code namespace} combined with the namespace prefix for this instance.
   */
  @VisibleForTesting
  private String getAlternateNamespace(String namespace) {
    return namespacePrefix + Strings.nullToEmpty(namespace);
  }

  /**
   * Returns {@code namespace} with the prefix removed.
   */
  @VisibleForTesting
  private String getOriginalNamespace(String namespace) {
    checkNotNull(namespace);
    checkArgument(namespace.startsWith(namespacePrefix),
        "%s must start with %s", namespace, namespacePrefix);
    return namespace.substring(namespacePrefix.length());
  }

  /**
   * Creates a copy of {@code entities}, where each entity is replaced with a copy whose key is in
   * the alternate namespace.
   */
  private List<Entity> getAlternateNamespaceEntities(Iterable<Entity> entities) {
    checkNotNull(entities);
    List<Entity> newEntities = Lists.newArrayList();
    for (Entity entity : entities) {
      newEntities.add(getAlternateNamespaceEntity(entity));
    }
    return newEntities;
  }

  /**
   * Creates a copy of {@code entity}, where the key is in the alternate namespace.
   */
  private Entity getAlternateNamespaceEntity(Entity entity) {
    checkNotNull(entity);
    return cloneEntityWithNewKey(entity, getAlternateNamespaceKey(entity.getKey()));
  }

  /**
   * Creates a copy of {@code entity}, where the key is in the original namespace.
   */
  Entity getOriginalNamespaceEntity(Entity entity) {
    checkNotNull(entity);
    return cloneEntityWithNewKey(entity, getOriginalNamespaceKey(entity.getKey()));
  }

  /**
   * Creates a copy of {@code entity}, where the key is replaced with {@code newKey}.
   */
  private Entity cloneEntityWithNewKey(Entity entity, Key newKey) {
    Entity newEntity = new Entity(newKey);
    newEntity.setPropertiesFrom(entity);
    return newEntity;
  }

  /**
   * Creates a copy of {@code entityMap}, where all the keys are replaced with the equivalent keys
   * in the original namespace.
   */
  private Map<Key, Entity> getOriginalNamespaceEntityMap(Map<Key, Entity> entityMap) {
    checkNotNull(entityMap);
    Map<Key, Entity> newMap = Maps.newHashMapWithExpectedSize(entityMap.size());
    for (Map.Entry<Key, Entity> entry : entityMap.entrySet()) {
      newMap.put(getOriginalNamespaceKey(entry.getKey()),
          getOriginalNamespaceEntity(entry.getValue()));
    }
    return newMap;
  }

  /**
   * Creates a copy of {@code key}, but in the alternate namespace.
   */
  private Key getAlternateNamespaceKey(Key key) {
    checkNotNull(key);
    String currentNamespace = NamespaceManager.get();
    try {
      NamespaceManager.set(getAlternateNamespace(key.getNamespace()));
      return cloneKey(key);
    } finally {
      NamespaceManager.set(currentNamespace);
    }
  }

  /**
   * Creates a copy of {@code key} in the original namespace. {@code key} must be in the alternate
   * namespace to begin with.
   */
  private Key getOriginalNamespaceKey(Key key) {
    checkNotNull(key);
    String currentNamespace = NamespaceManager.get();
    try {
      NamespaceManager.set(getOriginalNamespace(key.getNamespace()));
      return cloneKey(key);
    } finally {
      NamespaceManager.set(currentNamespace);
    }
  }

  /**
   * Creates a copy of {@code keys}, where each key is replaced with a copy that is in the alternate
   * namespace.
   */
  private List<Key> getAlternateNamespaceKeys(Iterable<Key> keys) {
    checkNotNull(keys);
    List<Key> newKeys = Lists.newArrayList();
    for (Key key : keys) {
      newKeys.add(getAlternateNamespaceKey(key));
    }
    return newKeys;
  }

  /**
   * Creates a copy of {@code keys}, where each key is replaced with a copy that is in the original
   * namespace. The elements of {@code keys} must be in the alternate namespace to begin with.
   */
  private List<Key> getOriginalNamespaceKeys(Iterable<Key> keys) {
    checkNotNull(keys);
    List<Key> newKeys = Lists.newArrayList();
    for (Key key : keys) {
      newKeys.add(getOriginalNamespaceKey(key));
    }
    return newKeys;
  }

  /**
   * Creates a copy of {@code query}, where each component key is replaced with the equivalent key
   * in the alternate namespace.
   */
  private Query getAlternateNamespaceQuery(Query query) {
    checkNotNull(query);
    Query alternateQuery;
    String currentNamespace = NamespaceManager.get();
    try {
      NamespaceManager.set(getAlternateNamespace(query.getNamespace()));
      alternateQuery = new Query(query.getKind());
    } finally {
      NamespaceManager.set(currentNamespace);
    }

    Key ancestor = query.getAncestor();
    if (ancestor != null) {
      alternateQuery.setAncestor(getAlternateNamespaceKey(ancestor));
    }
    for (SortPredicate sp : query.getSortPredicates()) {
      alternateQuery.addSort(sp.getPropertyName(), sp.getDirection());
    }
    alternateQuery.setFilter(query.getFilter());
    for (FilterPredicate fp : query.getFilterPredicates()) {
      alternateQuery.addFilter(fp.getPropertyName(), fp.getOperator(), fp.getValue());
    }
    if (query.isKeysOnly()) {
      alternateQuery.setKeysOnly();
    }
    for (Projection p : query.getProjections()) {
      alternateQuery.addProjection(p);
    }
    alternateQuery.setDistinct(query.getDistinct());

    return alternateQuery;
  }
/**
   * Creates a copy of {@code keyRange}, where the returned keys are in the alternate namespace.
   */

  /**
   * Creates a copy of {@code keyRange}, where the returned keys are in the original namespace,
   * rather than the alternate namespace.
   */
  private KeyRange getOriginalNamespaceKeyRange(KeyRange keyRange) {
    Key alternateParent = keyRange.getStart().getParent();
    Key originalParent = alternateParent == null ? null : getOriginalNamespaceKey(alternateParent);
    String currentNamespace = NamespaceManager.get();
    try {
      NamespaceManager.set(getOriginalNamespace(keyRange.getStart().getNamespace()));
      return new KeyRange(originalParent, keyRange.getStart().getKind(),
          keyRange.getStart().getId(), keyRange.getEnd().getId());
    } finally {
      NamespaceManager.set(currentNamespace);
    }
  }

  /**
   * Gets a version of the Datastore that is linked to {@code txn}.
   */
  private AsyncDatastoreService getTxnAsyncDatastore( Transaction txn) {
    return new TransactionLinkedAsyncDatastoreServiceImpl(datastore, txn);
  }

  /**
   * Creates a copy of {@code key}, where the copy (and each of its components) takes its namespace
   * from the {@link NamespaceManager}.
   */
  private static Key cloneKey(Key key) {
    checkNotNull(key);
    Key parentKey = key.getParent();
    String name = key.getName();
    long id = key.getId();
    if (parentKey == null) {
      if (name != null) {
        return new Entity(key.getKind(), name).getKey();
      } else if (id != 0L) {
        return new Entity(key.getKind(), key.getId()).getKey();
      } else {
        return new Entity(key.getKind()).getKey();
      }
    } else {
      if (name != null) {
        return new Entity(key.getKind(), name, cloneKey(parentKey)).getKey();
      } else if (id != 0L) {
        return new Entity(key.getKind(), key.getId(), cloneKey(parentKey)).getKey();
      } else {
        return new Entity(key.getKind(), cloneKey(parentKey)).getKey();
      }
    }
  }
}
