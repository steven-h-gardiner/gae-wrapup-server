// Copyright 2008 Google Inc. All Rights Reserved.
package com.google.appengine.api.datastore;

import com.google.appengine.api.utils.FutureWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * State and behavior that is common to all {@link Transaction} implementations.
 *
 * Our implementation is implicitly async. BeginTransaction RPCs always return
 * instantly, and this class maintains a reference to the {@link Future}
 * associated with the RPC.  We service as much of the {@link Transaction}
 * interface as we can without retrieving the result of the future.
 *
 * There is no synchronization in this code because transactions are associated
 * with a single thread and are documented as such.
 */
class TransactionImpl implements Transaction, CurrentTransactionProvider {

  private static final Logger logger = Logger.getLogger(TransactionImpl.class.getName());

  /**
   * Interface to a coupled object which handles the actual transaction RPCs
   * and other service protocol dependent details.
   */
  interface InternalTransaction {
    /**
     * Issues an asynchronous RPC to commit this transaction.
     */
    Future<Void> doCommitAsync();

    /**
     * Issues an asynchronous RPC to rollback this transaction.
     */
    Future<Void> doRollbackAsync();

    String getId();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
  }

  enum TransactionState {
    BEGUN,
    COMPLETION_IN_PROGRESS,
    COMMITTED,
    ROLLED_BACK,
    ERROR
  }

  private final String app;

  private final TransactionStack txnStack;

  private final DatastoreCallbacks callbacks;

  private final boolean isExplicit;

  private final InternalTransaction internalTransaction;

  TransactionState state = TransactionState.BEGUN;

  /**
   * A {@link PostOpFuture} implementation that runs both post put and post
   * delete callbacks.
   */
  private class PostCommitFuture extends PostOpFuture<Void> {
    private final List<Entity> putEntities;
    private final List<Key> deletedKeys;

    private PostCommitFuture(
        List<Entity> putEntities, List<Key> deletedKeys, Future<Void> delegate) {
      super(delegate, callbacks);
      this.putEntities = putEntities;
      this.deletedKeys = deletedKeys;
    }

    @Override
    void executeCallbacks(Void ignoreMe) {
      PutContext putContext = new PutContext(TransactionImpl.this, putEntities);
      callbacks.executePostPutCallbacks(putContext);
      DeleteContext deleteContext = new DeleteContext(TransactionImpl.this, deletedKeys);
      callbacks.executePostDeleteCallbacks(deleteContext);
    }
  }

  TransactionImpl(
      String app,
      TransactionStack txnStack,
      DatastoreCallbacks callbacks,
      boolean isExplicit,
      InternalTransaction txnProvider) {
    this.app = app;
    this.txnStack = txnStack;
    this.callbacks = callbacks;
    this.isExplicit = isExplicit;
    this.internalTransaction = txnProvider;
  }

  @Override
  public String getId() {
    return internalTransaction.getId();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof TransactionImpl) {
      return internalTransaction.equals(((TransactionImpl) o).internalTransaction);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return internalTransaction.hashCode();
  }

  @Override
  public void commit() {
    FutureHelper.quietGet(commitAsync());
  }

  @Override
  public Future<Void> commitAsync() {
    ensureTxnActive(this);
    try {
      List<RuntimeException> exceptions = new ArrayList<>();
      for (Future<?> f : txnStack.getFutures(this)) {
        try {
          FutureHelper.quietGet(f);
        } catch (RuntimeException e) {
          exceptions.add(e);
        }
      }
      if (!exceptions.isEmpty()) {
        for (int i = 1; i < exceptions.size(); i++) {
          RuntimeException e = exceptions.get(i);
          logger.log(Level.WARNING, "Failure while waiting to commit", e);
        }
        throw exceptions.get(0);
      }
      Future<Void> commitResponse = internalTransaction.doCommitAsync();
      state = TransactionState.COMPLETION_IN_PROGRESS;
      Future<Void> result = new FutureWrapper<Void, Void>(commitResponse) {
        @Override
        protected Void wrap(Void ignore) throws Exception {
          state = TransactionState.COMMITTED;
          return null;
        }

        @Override
        protected Throwable convertException(Throwable cause) {
          state = TransactionState.ERROR;
          return cause;
        }
      };
      return new PostCommitFuture(txnStack.getPutEntities(this), txnStack.getDeletedKeys(this),
          result);
    } finally {
      if (isExplicit) {
        txnStack.remove(this);
      }
    }
  }

  @Override
  public void rollback() {
    FutureHelper.quietGet(rollbackAsync());
  }

  @Override
  public Future<Void> rollbackAsync() {
    ensureTxnActive(this);
    try {
      for (Future<?> f : txnStack.getFutures(this)) {
        try {
          FutureHelper.quietGet(f);
        } catch (RuntimeException e) {
          logger.log(Level.INFO, "Failure while waiting to rollback", e);
        }
      }
      Future<Void> future = internalTransaction.doRollbackAsync();
      state = TransactionState.COMPLETION_IN_PROGRESS;
      return new FutureWrapper<Void, Void>(future) {
        @Override
        protected Void wrap(Void ignore) throws Exception {
          state = TransactionState.ROLLED_BACK;
          return null;
        }

        @Override
        protected Void absorbParentException(Throwable cause) throws Throwable {
          logger.log(Level.INFO, "Rollback of transaction failed", cause);
          state = TransactionState.ERROR;
          return null;
        }

        @Override
        protected Throwable convertException(Throwable cause) {
          state = TransactionState.ERROR;
          return cause;
        }
      };
    } finally {
      if (isExplicit) {
        txnStack.remove(this);
      }
    }
  }

  @Override
  public String getApp() {
    return app;
  }

  @Override
  public boolean isActive() {
    return state == TransactionState.BEGUN;
  }

  @Override
  public Transaction getCurrentTransaction(Transaction defaultValue) {
    return this;
  }

  /**
   * If {@code txn} is not null and not active, throw
   * {@link IllegalStateException}.
   */
  static void ensureTxnActive(Transaction txn) {
    if (txn != null && !txn.isActive()) {
      throw new IllegalStateException("Transaction with which this operation is "
          + "associated is not active.");
    }
  }

  @Override
  public String toString() {
    return "Txn [" + app + "." + getId() + ", " + state + "]";
  }
}
