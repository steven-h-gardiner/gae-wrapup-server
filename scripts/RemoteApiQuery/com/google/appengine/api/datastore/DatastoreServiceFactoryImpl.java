// Copyright 2012 Google Inc. All rights reserved.

package com.google.appengine.api.datastore;

/**
 * Creates DatastoreService instances.
 *
 */
final class DatastoreServiceFactoryImpl implements IDatastoreServiceFactory {

  @Override
  public DatastoreService getDatastoreService(DatastoreServiceConfig config) {
    return new DatastoreServiceImpl(getAsyncDatastoreService(config));
  }

  @Override
  public AsyncDatastoreServiceInternal getAsyncDatastoreService(DatastoreServiceConfig config) {
    TransactionStack txnStack = new TransactionStackImpl();
    if (DatastoreServiceGlobalConfig.getConfig().useApiProxy()) {
      return new AsyncDatastoreServiceImpl(config, config.constructApiConfig(), txnStack);
    } else {
      return new AsyncCloudDatastoreV1ServiceImpl(
          config, CloudDatastoreV1ClientImpl.create(), txnStack);
    }
  }
}
