// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.appengine.api;

import com.google.apphosting.api.ApiProxy;
import java.util.concurrent.ThreadFactory;

/**
 * {@code ThreadManager} exposes a {@link ThreadFactory} that allows
 * App Engine applications to spawn new threads.
 *
 * Refer to <a href="https://cloud.google.com/appengine/docs/java/runtime#threads">
 * this discussion of threads</a> for drawbacks of thread usage and possible
 * alternatives.
 *
 */
public final class ThreadManager {
  private static final String REQUEST_THREAD_FACTORY_ATTR =
      "com.google.appengine.api.ThreadManager.REQUEST_THREAD_FACTORY";

  private static final String BACKGROUND_THREAD_FACTORY_ATTR =
      "com.google.appengine.api.ThreadManager.BACKGROUND_THREAD_FACTORY";

  /**
   * Returns a {@link ThreadFactory} that will create threads scoped
   * to the current request.  These threads will be interrupted at the
   * end of the current request and must complete within the request
   * deadline.
   *
   * <p>Your code has limited access to the threads created by this
   * {@link ThreadFactory}.  For example, you can call
   * {@link Thread#setUncaughtExceptionHandler} and
   * {@link Thread#interrupt}, but not {@link Thread#stop} or any
   * other methods that require
   * {@code RuntimePermission("modifyThread")}.
   *
   * <p>The Java runtime will throw java.lang.IllegalStateException
   * if you try to create more than 50 threads in a single request.
   *
   * <p>Note that calling {@link ThreadFactory#newThread} on the
   * returned instance may throw any of the unchecked exceptions
   * mentioned by {@link #createBackgroundThread}.
   *
   * @throws NullPointerException if the calling thread is not associated with a request.
   */
  public static ThreadFactory currentRequestThreadFactory() {
    ApiProxy.Environment environment = getCurrentEnvironmentOrThrow();
    return (ThreadFactory) environment.getAttributes().get(REQUEST_THREAD_FACTORY_ATTR);
  }

  /**
   * Create a new {@link Thread} that executes {@code runnable} for
   * the duration of the current request.  Calling this method is
   * equivalent to invoking {@link ThreadFactory#newThread} on the
   * ThreadFactory returned from {@link #currentRequestThreadFactory}.
   * This thread will be interrupted at the end of the current request
   * and must complete within the request deadline.
   *
   * @throws IllegalStateException if you try to create more than 50 threads in a single request.
   * @throws NullPointerException if the calling thread is not associated with a request.
   * @throws ApiProxy.FeatureNotEnabledException If this application
   *     cannot use this feature.
   */
  public static Thread createThreadForCurrentRequest(Runnable runnable) {
    return currentRequestThreadFactory().newThread(runnable);
  }

  /**
   * Returns a {@link ThreadFactory} that will create threads that are
   * independent of the current request.
   *
   * <p>This ThreadFactory can currently only be used by backends.
   *
   * <p>Note that calling {@link ThreadFactory#newThread} on the
   * returned instance may throw any of the unchecked exceptions
   * mentioned by {@link #createBackgroundThread}.
   */
  public static ThreadFactory backgroundThreadFactory() {
    ApiProxy.Environment environment = getCurrentEnvironmentOrThrow();
    return (ThreadFactory) environment.getAttributes().get(BACKGROUND_THREAD_FACTORY_ATTR);
  }

  /**
   * Create a new {@link Thread} that executes {@code runnable}
   * independent of the current request.  Calling this method is
   * equivalent to invoking {@link ThreadFactory#newThread} on the
   * ThreadFactory returned from {@link #backgroundThreadFactory}.
   *
   * <p>This method can currently only be used by backends.
   *
   * @throws ApiProxy.FeatureNotEnabledException If this application
   *     cannot use this feature.
   * @throws ApiProxy.CancelledException If the request was interrupted
   *     while creating the new thread.
   * @throws ApiProxy.ApiDeadlineExceededException If creation of the
   *     new thread took too long.
   */
  public static Thread createBackgroundThread(Runnable runnable) {
    return backgroundThreadFactory().newThread(runnable);
  }

  private static ApiProxy.Environment getCurrentEnvironmentOrThrow() {
    ApiProxy.Environment environment = ApiProxy.getCurrentEnvironment();
    if (environment == null) {
      throw new NullPointerException(
          "Current thread is not associated with any request and is not a background thread");
    }
    return environment;
  }
}