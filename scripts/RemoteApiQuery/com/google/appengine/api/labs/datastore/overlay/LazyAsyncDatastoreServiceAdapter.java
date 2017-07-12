package com.google.appengine.api.labs.datastore.overlay;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreAttributes;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Index;
import com.google.appengine.api.datastore.Index.IndexState;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * An implementation of {@link AsyncDatastoreService} in terms of {@link DatastoreService}, where
 * {@code Future} operations are only evaluated on {@code get()}.
 */
final class LazyAsyncDatastoreServiceAdapter extends DelegatedBaseDatastoreService
    implements AsyncDatastoreService {

  private final DatastoreService datastore;

  public LazyAsyncDatastoreServiceAdapter(DatastoreService datastore) {
    super(datastore);
    this.datastore = datastore;
  }

  @Override
  public Future<Entity> get(final Key key) {
    return new FakeFutureTask<Entity>() {
      @Override
      protected Entity call() throws Exception {
        return datastore.get(key);
      }
    };
  }

  @Override
  public Future<Entity> get(final Transaction txn, final Key key) {
    return new FakeFutureTask<Entity>() {
      @Override
      protected Entity call() throws Exception {
        return datastore.get(txn, key);
      }
    };
  }

  @Override
  public Future<Map<Key, Entity>> get(final Iterable<Key> keys) {
    return new FakeFutureTask<Map<Key, Entity>>() {
      @Override
      protected Map<Key, Entity> call() throws Exception {
        return datastore.get(keys);
      }
    };
  }

  @Override
  public Future<Map<Key, Entity>> get(final Transaction txn, final Iterable<Key> keys) {
    return new FakeFutureTask<Map<Key, Entity>>() {
      @Override
      protected Map<Key, Entity> call() throws Exception {
        return datastore.get(txn, keys);
      }
    };
  }

  @Override
  public Future<Key> put(final Entity entity) {
    return new FakeFutureTask<Key>() {
      @Override
      protected Key call() throws Exception {
        return datastore.put(entity);
      }
    };
  }

  @Override
  public Future<Key> put(final Transaction txn, final Entity entity) {
    return new FakeFutureTask<Key>() {
      @Override
      protected Key call() throws Exception {
        return datastore.put(txn, entity);
      }
    };
  }

  @Override
  public Future<List<Key>> put(final Iterable<Entity> entities) {
    return new FakeFutureTask<List<Key>>() {
      @Override
      protected List<Key> call() throws Exception {
        return datastore.put(entities);
      }
    };
  }

  @Override
  public Future<List<Key>> put(final Transaction txn, final Iterable<Entity> entities) {
    return new FakeFutureTask<List<Key>>() {
      @Override
      protected List<Key> call() throws Exception {
        return datastore.put(txn, entities);
      }
    };
  }

  @Override
  public Future<Void> delete(final Key... keys) {
    return new FakeFutureTask<Void>() {
      @Override
      protected Void call() throws Exception {
        datastore.delete(keys);
        return null;
      }
    };
  }

  @Override
  public Future<Void> delete(final Transaction txn, final Key... keys) {
    return new FakeFutureTask<Void>() {
      @Override
      protected Void call() throws Exception {
        datastore.delete(txn, keys);
        return null;
      }
    };
  }

  @Override
  public Future<Void> delete(final Iterable<Key> keys) {
    return new FakeFutureTask<Void>() {
      @Override
      protected Void call() throws Exception {
        datastore.delete(keys);
        return null;
      }
    };
  }

  @Override
  public Future<Void> delete(final Transaction txn, final Iterable<Key> keys) {
    return new FakeFutureTask<Void>() {
      @Override
      protected Void call() throws Exception {
        datastore.delete(txn, keys);
        return null;
      }
    };  }

  @Override
  public Future<Transaction> beginTransaction() {
    return new FakeFutureTask<Transaction>() {
      @Override
      protected Transaction call() throws Exception {
        return datastore.beginTransaction();
      }
    };
  }

  @Override
  public Future<Transaction> beginTransaction(final TransactionOptions options) {
    return new FakeFutureTask<Transaction>() {
      @Override
      protected Transaction call() throws Exception {
        return datastore.beginTransaction(options);
      }
    };
  }

  @Override
  public Future<KeyRange> allocateIds(final String kind, final long num) {
    return new FakeFutureTask<KeyRange>() {
      @Override
      protected KeyRange call() throws Exception {
        return datastore.allocateIds(kind, num);
      }
    };
  }

  @Override
  public Future<KeyRange> allocateIds(final Key parent, final String kind, final long num) {
    return new FakeFutureTask<KeyRange>() {
      @Override
      protected KeyRange call() throws Exception {
        return datastore.allocateIds(parent, kind, num);
      }
    };
  }

  @Override
  public Future<DatastoreAttributes> getDatastoreAttributes() {
    return new FakeFutureTask<DatastoreAttributes>() {
      @Override
      protected DatastoreAttributes call() throws Exception {
        return datastore.getDatastoreAttributes();
      }
    };
  }

  @Override
  public Future<Map<Index, IndexState>> getIndexes() {
    return new FakeFutureTask<Map<Index, IndexState>>() {
      @Override
      protected Map<Index, IndexState> call() throws Exception {
        return datastore.getIndexes();
      }
    };
  }
}
