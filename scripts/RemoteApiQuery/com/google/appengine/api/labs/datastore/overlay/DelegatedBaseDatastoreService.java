package com.google.appengine.api.labs.datastore.overlay;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.appengine.api.datastore.BaseDatastoreService;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

import java.util.Collection;

/**
 * An implementation of {@link BaseDatastoreService} in terms of another, delegated
 * {@link BaseDatastoreService}.
 */
class DelegatedBaseDatastoreService implements BaseDatastoreService {
  private final BaseDatastoreService datastore;

  public DelegatedBaseDatastoreService(BaseDatastoreService datastore) {
    this.datastore = checkNotNull(datastore);
  }

  @Override
  public PreparedQuery prepare(Query query) {
    return datastore.prepare(query);
  }

  @Override
  public PreparedQuery prepare(Transaction txn, Query query) {
    return datastore.prepare(txn, query);
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

}
