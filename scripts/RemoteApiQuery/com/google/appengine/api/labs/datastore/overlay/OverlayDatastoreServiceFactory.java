package com.google.appengine.api.labs.datastore.overlay;

import static com.google.appengine.api.datastore.DatastoreServiceConfig.Builder.withDefaults;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.IDatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A factory for creating overlay-based {@link DatastoreService} implementations.
 */
public final class OverlayDatastoreServiceFactory {

  private final IDatastoreServiceFactory baseFactory;
  private final NameFactory nameFactory;

  public OverlayDatastoreServiceFactory() {
    this(new IDatastoreServiceFactory() {
      @Override
      public DatastoreService getDatastoreService(DatastoreServiceConfig config) {
        return DatastoreServiceFactory.getDatastoreService(config);
      }

      @Override
      public AsyncDatastoreService getAsyncDatastoreService(DatastoreServiceConfig config) {
        return DatastoreServiceFactory.getAsyncDatastoreService(config);
      }
    });
  }

  OverlayDatastoreServiceFactory(IDatastoreServiceFactory baseFactory) {
    this(baseFactory, new NameFactory());
  }

  OverlayDatastoreServiceFactory(IDatastoreServiceFactory baseFactory,
      NameFactory nameFactory) {
    this.baseFactory = baseFactory;
    this.nameFactory = nameFactory;
  }

  /**
   * Creates a new overlay-based {@link DatastoreService} using {@code config}, where a single
   * Datastore holds both original and overlay data. The data is segregated using namespaces.
   */
  public OverlayDatastoreService createOverlayDatastoreService(
      DatastoreServiceConfig config) {
    checkNotNull(config);
    return createOverlayDatastoreService(baseFactory.getAsyncDatastoreService(config));
  }

  /**
   * Creates a new overlay-based {@link DatastoreService} using the default config, where a single
   * Datastore holds both original and overlay data. The data is segregated using namespaces.
   */
  public OverlayDatastoreService createOverlayDatastoreService() {
    return createOverlayDatastoreService(baseFactory.getAsyncDatastoreService(withDefaults()));
  }

  /**
   * Creates a new overlay-based {@link DatastoreService} named {@code name} using the default
   * config, where a single Datastore holds both original and overlay data. The data is segregated
   * using namespaces.
   */
  public OverlayDatastoreService createOverlayDatastoreService(String name) {
    return createOverlayDatastoreService(baseFactory.getAsyncDatastoreService(withDefaults()),
        name);
  }

  /**
   * Creates a new overlay-based {@link DatastoreService} using {@code datastore},
   * where a single Datastore holds both original and overlay data. The data is segregated using
   * namespaces.
   */
  public OverlayDatastoreService createOverlayDatastoreService(AsyncDatastoreService datastore) {
    checkNotNull(datastore);
    return new SyncOverlayDatastoreServiceAdapter(createOverlayAsyncDatastoreService(datastore));
  }

  /**
   * Creates a new overlay-based {@link DatastoreService} using {@code datastore}, where a single
   * Datastore holds both original and overlay data. The data is segregated using namespaces.
   *
   * NOTE: An overlay created by this method will be significantly less performant than an overlay
   * initialized with an {@code AsyncDatastoreService}. A single overlay operation may require
   * several operations on the parent Datastore, which must be serialized if the parent is
   * synchronous. It is recommended to initialize an overlay with an {@code AsyncDatastoreService}
   * if possible.
   */
  public OverlayDatastoreService createOverlayDatastoreService(DatastoreService datastore) {
    checkNotNull(datastore);
    return createOverlayDatastoreService(new LazyAsyncDatastoreServiceAdapter(datastore));
  }

  /**
   * Creates an overlay-based {@link DatastoreService} named {@code name} using {@code datastore},
   * where a single Datastore holds both original and overlay data. The data is segregated using
   * namespaces.
   *
   * The name/datastore pair uniquely defines an overlay. If an overlay named {@code name} exists,
   * the returned {@code DatastoreService} will be connected to the existing overlay; otherwise, a
   * new overlay will be created.
   */
  public OverlayDatastoreService createOverlayDatastoreService(AsyncDatastoreService datastore,
      String name) {
    checkNotNull(datastore);
    checkNotNull(name);
    return new SyncOverlayDatastoreServiceAdapter(
        createOverlayAsyncDatastoreService(datastore, name));
  }

  /**
   * Creates an overlay-based {@link DatastoreService} named {@code name} using {@code datastore},
   * where a single Datastore holds both original and overlay data. The data is segregated using
   * namespaces.
   *
   * The name/datastore pair uniquely defines an overlay. If an overlay named {@code name} exists,
   * the returned {@code DatastoreService} will be connected to the existing overlay; otherwise, a
   * new overlay will be created.
   *
   * NOTE: An overlay created by this method will be significantly less performant than an overlay
   * initialized with an {@code AsyncDatastoreService}. A single overlay operation may require
   * several operations on the parent Datastore, which must be serialized if the parent is
   * synchronous. It is recommended to initialize an overlay with an {@code AsyncDatastoreService}
   * if possible.
   */
  public OverlayDatastoreService createOverlayDatastoreService(DatastoreService datastore,
      String name) {
    checkNotNull(datastore);
    checkNotNull(name);
    return createOverlayDatastoreService(new LazyAsyncDatastoreServiceAdapter(datastore), name);
  }

  /**
   * Creates an overlay-based {@link DatastoreService} named {@code name} using {@code datastore},
   * where a single Datastore holds both original and overlay data. The data is segregated using
   * namespaces.
   *
   * The name/datastore pair uniquely defines an overlay. If an overlay named {@code name} exists,
   * the returned {@code DatastoreService} will be connected to the existing overlay; otherwise, a
   * new overlay will be created iff {@code create} is true.
   *
   * @raises IllegalStateException if {@code create} is false and the overlay does not already
   *         exist.
   */
  public OverlayDatastoreService createOverlayDatastoreService(AsyncDatastoreService datastore,
      String name, boolean create) throws IllegalStateException {
    checkNotNull(datastore);
    checkNotNull(name);
    return new SyncOverlayDatastoreServiceAdapter(
        createOverlayAsyncDatastoreService(datastore, name, create));
  }

  /**
   * Creates an overlay-based {@link DatastoreService} named {@code name} using {@code datastore},
   * where a single Datastore holds both original and overlay data. The data is segregated using
   * namespaces.
   *
   * The name/datastore pair uniquely defines an overlay. If an overlay named {@code name} exists,
   * the returned {@code DatastoreService} will be connected to the existing overlay; otherwise, a
   * new overlay will be created iff {@code create} is true.
   *
   * NOTE: An overlay created by this method will be significantly less performant than an overlay
   * initialized with an {@code AsyncDatastoreService}. A single overlay operation may require
   * several operations on the parent Datastore, which must be serialized if the parent is
   * synchronous. It is recommended to initialize an overlay with an {@code AsyncDatastoreService}
   * if possible.
   *
   * @raises IllegalStateException if {@code create} is false and the overlay does not already
   *         exist.
   */
  public OverlayDatastoreService createOverlayDatastoreService(DatastoreService datastore,
      String name, boolean create) throws IllegalStateException {
    checkNotNull(datastore);
    checkNotNull(name);
    return createOverlayDatastoreService(new LazyAsyncDatastoreServiceAdapter(datastore), name,
        create);
  }

  /**
   * Creates a new overlay-based {@link AsyncDatastoreService} using {@code config}, where a
   * single Datastore holds both original and overlay data. The data is segregated using namespaces.
   */
  public OverlayAsyncDatastoreService createOverlayAsyncDatastoreService(
      DatastoreServiceConfig config) {
    checkNotNull(config);
    return createOverlayAsyncDatastoreService(baseFactory.getAsyncDatastoreService(config));
  }

  /**
   * Creates a new overlay-based {@link AsyncDatastoreService} using the default config, where a
   * single Datastore holds both original and overlay data. The data is segregated using namespaces.
   */
  public OverlayAsyncDatastoreService createOverlayAsyncDatastoreService() {
    return createOverlayAsyncDatastoreService(baseFactory.getAsyncDatastoreService(withDefaults()));
  }

  /**
   * Creates a new overlay-based {@link AsyncDatastoreService} named {@code name} using the default
   * config, where a single Datastore holds both original and overlay data. The data is segregated
   * using namespaces.
   */
  public OverlayAsyncDatastoreService createOverlayAsyncDatastoreService(String name) {
    return createOverlayAsyncDatastoreService(
        baseFactory.getAsyncDatastoreService(withDefaults()), name);
  }

  /**
   * Creates a new overlay-based {@link AsyncDatastoreService} using {@code datastore}, where a
   * single Datastore holds both original and overlay data. The data is segregated using namespaces.
   */
  public OverlayAsyncDatastoreService createOverlayAsyncDatastoreService(
      AsyncDatastoreService datastore) {
    checkNotNull(datastore);
    String name = getUniqueOverlayName(new SyncDatastoreServiceAdapter(datastore));
    return createOverlayAsyncDatastoreService(datastore, name);
  }

  /**
   * Creates an overlay-based {@link AsyncDatastoreService} named {@code name} using
   * {@code datastore}, where a single Datastore holds both original and overlay data. The data is
   * segregated using namespaces.
   *
   * The name/datastore pair uniquely defines an overlay. If an overlay named {@code name} exists,
   * the returned {@code DatastoreService} will be connected to the existing overlay; otherwise, a
   * new overlay will be created.
   */
  public OverlayAsyncDatastoreService createOverlayAsyncDatastoreService(
      AsyncDatastoreService datastore, String name) {
    checkNotNull(datastore);
    checkNotNull(name);
    try {
      return createOverlayAsyncDatastoreService(datastore, name, true);
    } catch (IllegalStateException e) {
      throw new AssertionError(
          "Unexpected IllegalStateException on overlay creation when create=true.", e);
    }
  }

  /**
   * Creates an overlay-based {@link AsyncDatastoreService} named {@code name} using
   * {@code datastore}, where a single Datastore holds both original and overlay data. The data is
   * segregated using namespaces.
   *
   * The name/datastore pair uniquely defines an overlay. If an overlay named {@code name} exists,
   * the returned {@code DatastoreService} will be connected to the existing overlay; otherwise, a
   * new overlay will be created iff {@code create} is true.
   *
   * @raises IllegalStateException if {@code create} is false and the overlay does not already
   *         exist.
   */
  public OverlayAsyncDatastoreService createOverlayAsyncDatastoreService(
      AsyncDatastoreService datastore, String name, boolean create) throws IllegalStateException {
    checkNotNull(datastore);
    checkNotNull(name);
    DatastoreService syncWrappedDatastore = new SyncDatastoreServiceAdapter(datastore);
    if (!overlayExists(syncWrappedDatastore, name, null)) {
      if (create) {
        registerOverlay(syncWrappedDatastore, name, null);
      } else {
        throw new IllegalStateException("Overlay \"" + name + "\" does not exist.");
      }
    }
    return new OverlayAsyncDatastoreServiceImpl(
        name,
        new NamespacePinnedAsyncDatastoreServiceImpl(datastore, getOverlayNamespace(name), true),
        datastore);
  }

  static class NameFactory {
    private static final char[] ALPHA_NUMERIC_CHARS =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final int RANDOM_NAME_LENGTH = 8;

    public String getRandomName() {
      char[] buf = new char[RANDOM_NAME_LENGTH];
      ThreadLocalRandom random = ThreadLocalRandom.current();
      for (int i = 0; i < RANDOM_NAME_LENGTH; i++) {
        buf[i] = ALPHA_NUMERIC_CHARS[random.nextInt(ALPHA_NUMERIC_CHARS.length)];
      }
      return new String(buf);
    }
  }

  private static final String OVERLAY_METADATA_NAMESPACE = "_overlay_";

  private static final String OVERLAY_KIND = "_overlay_";

  private String getUniqueOverlayName(DatastoreService datastore) {
    while (true) {
      Transaction txn = datastore.beginTransaction();
      try {
        String name = nameFactory.getRandomName();
        if (!overlayExists(datastore, name, txn)) {
          registerOverlay(datastore, name, txn);
          txn.commit();
          return name;
        }
      } finally {
        if (txn.isActive()) {
          txn.rollback();
        }
      }
    }
  }

  /**
   * Returns a name for an overlay with the given {@code suffix}. This method is deterministic:
   * the name returned for a given suffix is stable.
   */
  private static String getOverlayNamespace(String suffix) {
    return OVERLAY_METADATA_NAMESPACE + suffix;
  }

  private static void registerOverlay(DatastoreService datastore, String name, Transaction txn) {
    String currentNamespace = NamespaceManager.get();
    try {
      NamespaceManager.set(OVERLAY_METADATA_NAMESPACE);
      Entity entity = new Entity(OVERLAY_KIND, name);
      datastore.put(txn, entity);
    } finally {
      NamespaceManager.set(currentNamespace);
    }
  }

  private static boolean overlayExists(DatastoreService datastore, String overlayName, Transaction txn) {
    String currentNamespace = NamespaceManager.get();
    try {
      NamespaceManager.set(OVERLAY_METADATA_NAMESPACE);
      Key key = KeyFactory.createKey(OVERLAY_KIND, overlayName);
      datastore.get(txn, key);
      return true;
    } catch (EntityNotFoundException e) {
      return false;
    } finally {
      NamespaceManager.set(currentNamespace);
    }
  }

}
