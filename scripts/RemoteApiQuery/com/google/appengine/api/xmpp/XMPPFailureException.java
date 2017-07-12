// Copyright 2008 Google Inc. All Rights Reserved.
package com.google.appengine.api.xmpp;

/**
 * {@code XMPPFailureException} is thrown when any unknown error occurs while communicating with the
 * XMPP service.
 *
 * @deprecated This API has been <a
 *     href="https://cloud.google.com/appengine/docs/deprecations/xmpp">deprecated</a>.
 */
@Deprecated
public class XMPPFailureException extends RuntimeException {
  public XMPPFailureException(String message) {
    super(message);
  }
}
