// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.appengine.api.taskqueue;

/**
 * Indicates a failure to create a task payload.  This is
 * most likely an issue with serialization.
 *
 */
public class DeferredTaskCreationException extends RuntimeException {
  private static final long serialVersionUID = -1434044266930229L;

  public DeferredTaskCreationException(Throwable e) {
    super(e);
  }
}
