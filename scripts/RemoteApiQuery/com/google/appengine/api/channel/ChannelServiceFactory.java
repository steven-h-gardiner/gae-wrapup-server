// Copyright 2010 Google Inc. All rights reserved.

package com.google.appengine.api.channel;

import com.google.appengine.spi.ServiceFactoryFactory;

/**
 * Constructs and instance of the Channel service.
 *
 * @deprecated This API has been <a
 *     href="https://cloud.google.com/appengine/docs/deprecations/channel">deprecated</a>.
 */
@Deprecated
public final class ChannelServiceFactory {

  /**
   * Creates an implementation of the ChannelService.
   */
  public static ChannelService getChannelService() {
    return getFactory().getChannelService();
  }

  private static IChannelServiceFactory getFactory() {
    return ServiceFactoryFactory.getFactory(IChannelServiceFactory.class);
  }
}
