// Copyright 2010 Google Inc. All rights reserved.

package com.google.appengine.api.channel;

/**
 * {@code ChannelFailureException} is an unchecked exception that is thrown for any unexpected error
 * that occurs while communicating with the channel service.
 *
 * @deprecated This API has been <a
 *     href="https://cloud.google.com/appengine/docs/deprecations/channel">deprecated</a>.
 */
@Deprecated
public final class ChannelFailureException extends RuntimeException {
  public ChannelFailureException(String message) {
    super(message);
  }

  public ChannelFailureException(String message, Throwable cause) {
    super(message, cause);
  }
}
