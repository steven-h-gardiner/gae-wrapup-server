package com.google.appengine.api.labs.datastore.overlay;

import com.google.appengine.api.datastore.BaseDatastoreService;

/**
 * An implementation of {@link OverlayDatastoreService} in terms of
 * {@link OverlayAsyncDatastoreService}.
 */
class SyncOverlayDatastoreServiceAdapter extends SyncDatastoreServiceAdapter
    implements OverlayDatastoreService {

  private final String name;
  private final BaseDatastoreService parent;

  public SyncOverlayDatastoreServiceAdapter(OverlayAsyncDatastoreService asyncDatastore) {
    super(asyncDatastore);
    name = asyncDatastore.getName();
    parent = asyncDatastore.getParentDatastoreService();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public BaseDatastoreService getParentDatastoreService() {
    return parent;
  }
}
