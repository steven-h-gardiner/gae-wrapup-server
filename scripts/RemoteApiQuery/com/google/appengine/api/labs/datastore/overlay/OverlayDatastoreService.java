package com.google.appengine.api.labs.datastore.overlay;

import com.google.appengine.api.datastore.DatastoreService;

/**
 * An overlay on a synchronous {@link DatastoreService}.
 */
public interface OverlayDatastoreService extends DatastoreService, OverlayBaseDatastoreService {}
