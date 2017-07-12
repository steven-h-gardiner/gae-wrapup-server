// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.appengine.api.xmpp;

/**
 * Class that represents an XMPP error.
 *
 * @deprecated This API has been <a
 *     href="https://cloud.google.com/appengine/docs/deprecations/xmpp">deprecated</a>.
 */
@Deprecated
public class Error {
  private final JID fromJid;
  private final String stanza;

  /**
   * Constructor for an Error object.
   * @param fromJid the sender of the error.
   * @param stanza the XMPP stanza representing the error.
   */
  public Error(JID fromJid, String stanza) {
    this.fromJid = fromJid;
    this.stanza = stanza;
  }

  public JID getFromJid() {
    return fromJid;
  }

  public String getStanza() {
    return stanza;
  }
}
