package com.google.appengine.api.labs.trace;

import com.google.appengine.spi.ServiceFactoryFactory;

/**
 * Creates a TraceService.
 */
public final class TraceServiceFactory {
  /**
   * Creates an implementation of the TraceService.
   */
  public static TraceService getTraceService() {
    return getFactory().getTraceService();
  }

  private TraceServiceFactory() {}

  private static ITraceServiceFactory getFactory() {
    return ServiceFactoryFactory.getFactory(ITraceServiceFactory.class);
  }
}
