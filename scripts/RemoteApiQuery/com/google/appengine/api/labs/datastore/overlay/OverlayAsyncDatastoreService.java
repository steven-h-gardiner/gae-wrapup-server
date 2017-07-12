package com.google.appengine.api.labs.datastore.overlay;

import com.google.appengine.api.datastore.AsyncDatastoreService;

/**
 * An overlay on an {@link AsyncDatastoreService}.
 */
public interface OverlayAsyncDatastoreService
    extends AsyncDatastoreService, OverlayBaseDatastoreService {}
