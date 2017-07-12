package com.google.appengine.api.labs.trace;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.CloudTrace;
import com.google.apphosting.api.CloudTraceContext;

import javax.annotation.Nullable;

class SpanImpl extends Span {
  private final CloudTraceContext parentContext;
  private final CloudTraceContext context;

  /**
   * Constructs a new span as a child of the given context.
   * @param parent context of the parent span.
   * @param name name of the new span.
   */
  SpanImpl(@Nullable CloudTraceContext parent, String name) {
    parentContext = parent;
    if (parentContext == null || !parentContext.isTraceEnabled()) {
      context = null;
    } else {
      context = CloudTrace.startChildSpan(ApiProxy.getCurrentEnvironment(), parentContext, name);
    }
  }

  /**
   * Constructs a new span as a child of the current context.
   * @param name name of the new span.
   */
  SpanImpl(String name) {
    this(CloudTrace.getCurrentContext(ApiProxy.getCurrentEnvironment()), name);
  }

  @Override
  public Span startChildSpan(String name) {
    return new SpanImpl(context, name);
  }

  @Override
  public Span setLabel(String key, String value) {
    if (context == null || !context.isTraceEnabled()) {
      return this;
    }
    CloudTrace.setLabel(ApiProxy.getCurrentEnvironment(), context, key, value);
    return this;
  }

  @Override
  public void endSpan() {
    if (context == null || !context.isTraceEnabled()) {
      return;
    }
    CloudTrace.endSpan(ApiProxy.getCurrentEnvironment(), context, parentContext);
  }

  @Override
  public void close() {
    endSpan();
  }

  @Override
  @Nullable
  public CloudTraceContext getParentContext() {
    return parentContext;
  }

  @Override
  @Nullable
  public CloudTraceContext getContext() {
    return context;
  }
}
