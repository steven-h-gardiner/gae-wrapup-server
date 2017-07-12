package com.google.appengine.api.labs.trace;

/**
 * {@code TraceService} allows callers to create custom spans.
 */
public interface TraceService {
  /**
   * Starts a new span as a child of the current span. The new span becomes current.
   *
   * @param name Name of the new span.
   * @return The new span.
   */
  Span startSpan(String name);
}
