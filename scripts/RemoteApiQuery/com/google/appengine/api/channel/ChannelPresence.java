// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.appengine.api.channel;

/**
 * Represents presence information returned by the server.
 *
 * @deprecated This API has been <a
 *     href="https://cloud.google.com/appengine/docs/deprecations/channel">deprecated</a>.
 */
@Deprecated
public final class ChannelPresence {

  private final boolean isConnected;
  private final String clientId;

  ChannelPresence(boolean isConnected, String clientId) {
    this.isConnected = isConnected;
    this.clientId = clientId;
  }

  public boolean isConnected() {
    return isConnected;
  }

  public String clientId() {
    return clientId;
  }
}
