package com.google.appengine.api.labs.trace;

/**
 * Creates a TraceService.
 */
final class TraceServiceFactoryImpl implements ITraceServiceFactory {
  @Override
  public TraceService getTraceService() {
    return new TraceServiceImpl();
  }
}