// Copyright 2008 Google Inc. All rights reserved.

package com.google.appengine.api.xmpp;

/**
 * Types of XMPP messages.
 *
 * @see <a href="http://tools.ietf.org/html/rfc3921#section-2.1.1">RFC 3921, Section 2.1.1</a> for
 *     the specification of XMPP message types.
 * @author kushal@google.com (Kushal Dave)
 * @deprecated This API has been <a
 *     href="https://cloud.google.com/appengine/docs/deprecations/xmpp">deprecated</a>.
 */
@Deprecated
public enum MessageType {
  CHAT,
  ERROR,
  GROUPCHAT,
  HEADLINE,
  NORMAL;

  String getInternalName() {
    return name().toLowerCase();
  }
}
