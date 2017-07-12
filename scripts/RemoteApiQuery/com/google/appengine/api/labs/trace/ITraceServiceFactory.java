package com.google.appengine.api.labs.trace;

/**
 * Creates {@link TraceService} implementations.
 */
public interface ITraceServiceFactory {
  /**
   * Creates a {@code TraceService}.
   */
  TraceService getTraceService();
}
