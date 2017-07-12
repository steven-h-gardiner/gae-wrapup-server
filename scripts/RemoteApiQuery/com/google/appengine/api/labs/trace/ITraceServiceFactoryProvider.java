package com.google.appengine.api.labs.trace;

import com.google.appengine.spi.FactoryProvider;
import com.google.appengine.spi.ServiceProvider;

/**
 * Factory provider for {@link ITraceServiceFactory}.
 *
 * <p><b>Note:</b> This class is not intended for end users.
 */
@ServiceProvider(value = FactoryProvider.class, precedence = Integer.MIN_VALUE)
public final class ITraceServiceFactoryProvider extends FactoryProvider<ITraceServiceFactory> {
  private final TraceServiceFactoryImpl implementation = new TraceServiceFactoryImpl();

  public ITraceServiceFactoryProvider() {
    super(ITraceServiceFactory.class);
  }

  @Override
  protected ITraceServiceFactory getFactoryInstance() {
    return implementation;
  }
}
