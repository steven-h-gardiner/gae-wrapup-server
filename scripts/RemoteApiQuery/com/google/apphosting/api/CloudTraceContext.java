package com.google.apphosting.api;

import java.util.Random;

/**
 * Stores tracing context including IDs and settings.
 */
public class CloudTraceContext {
  private static final long INVALID_SPAN_ID = 0;
  private static final long MASK_TRACE_ENABLED = 0x01;
  private static final Random random = new Random();

  private final byte[] traceId;
  private final long parentSpanId;
  private final long spanId;
  private final long traceMask;

  /**
   * Create a new context with the given parameters.
   * @param traceId id of the trace.
   * @param spanId current span id.
   * @param traceMask trace options.
   */
  public CloudTraceContext(byte[] traceId, long spanId, long traceMask) {
    this(traceId, INVALID_SPAN_ID, spanId, traceMask);
  }

  /**
   * Create a new context with the given parameters.
   * @param traceId id of the trace.
   * @paran parentSpanId parent span id.
   * @param spanId current span id.
   * @param traceMask trace options.
   */
  public CloudTraceContext(byte[] traceId, long parentSpanId, long spanId, long traceMask) {
    this.traceId = traceId;
    this.parentSpanId = parentSpanId;
    this.spanId = spanId;
    this.traceMask = traceMask;
  }

  /**
   * Creates a child context of the current context.
   */
  public CloudTraceContext createChildContext() {
    return new CloudTraceContext(traceId, spanId, random.nextLong(), getTraceMask());
  }

  public byte[] getTraceId() {
    return traceId;
  }

  public long getParentSpanId() {
    return parentSpanId;
  }

  public long getSpanId() {
    return spanId;
  }

  public long getTraceMask() {
    return traceMask;
  }

  public boolean isTraceEnabled() {
    return (traceMask & MASK_TRACE_ENABLED) != 0;
  }
}
