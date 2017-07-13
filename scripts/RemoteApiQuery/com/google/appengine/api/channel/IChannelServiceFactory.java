// Copyright 2012 Google Inc. All rights reserved.

package com.google.appengine.api.channel;

/**
 * Constructs and instance of the Channel service.
 *
 * @deprecated This API has been <a
 *     href="https://cloud.google.com/appengine/docs/deprecations/channel">deprecated</a>.
 */
@Deprecated
public interface IChannelServiceFactory {

  /**
   * Creates an implementation of the ChannelService.
   */
   ChannelService getChannelService();

}