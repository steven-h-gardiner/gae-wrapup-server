package com.google.appengine.api.labs.trace;

import com.google.apphosting.api.CloudTraceContext;

import javax.annotation.Nullable;

/**
 * {@code Span} allows users to manage details of a span: start a child, set a label, or
 * end a span.
 */
public abstract class Span implements AutoCloseable {
  /**
   * Start a new span as the child of the current span.
   * @param name name of the child span.
   * @return the child span.
   */
  public abstract Span startChildSpan(String name);

  /**
   * Sets a key:value label on the span.
   *
   * @param key Key of the label.
   * @param value Value of the label.
   * @return this {@code Span} object to allow for further chained method calls.
   */
  public abstract Span setLabel(String key, String value);

  /**
   * Ends the current span. The span is no longer current. Its parent span becomes current.
   *
   * <p>This must be the last call made to a span instance.
   */
  public abstract void endSpan();

  /**
   * Ends the current span. The span is no longer current. Its parent span becomes current.
   *
   * <p>This must be the last call made to a span instance.
   */
  @Override
  public abstract void close();

  /**
   * Gets the context of the parent span.
   * @return the context of the parent span.
   */
  @Nullable
  public abstract CloudTraceContext getParentContext();

  /**
   * Gets the context of this span.
   * @return the context of this span.
   */
  @Nullable
  public abstract CloudTraceContext getContext();
}
