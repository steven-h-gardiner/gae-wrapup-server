package com.google.appengine.api.labs.trace;

/**
 * The TraceService allows users to add custom trace spans.
 */
final class TraceServiceImpl implements TraceService {
  @Override
  public Span startSpan(String name) {
    return new SpanImpl(name);
  }
}
