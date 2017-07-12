package com.google.appengine.api.labs.datastore.overlay;

import com.google.appengine.api.datastore.BaseDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;

/**
 * A {@link DatastoreService} using an overlay model. Conceptually, an overlay Datastore is based on
 * some other Datastore (the "parent"). The overlay allows developers to effectively update or
 * delete entities on the parent, but without actually modifying the data that the parent stores.
 * (There is one exception: ID allocation is forwarded to the parent Datastore.)
 */
interface OverlayBaseDatastoreService extends BaseDatastoreService {
  /** Gets the name of the overlay. */
  String getName();

  /** Gets the parent Datastore for the overlay. */
  BaseDatastoreService getParentDatastoreService();
}
