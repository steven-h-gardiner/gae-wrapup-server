// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.appengine.api.xmpp;

import com.google.appengine.spi.ServiceFactoryFactory;

/**
 * Constructs an instance of the XMPP service.
 *
 * @deprecated This API has been <a
 *     href="https://cloud.google.com/appengine/docs/deprecations/xmpp">deprecated</a>.
 */
@Deprecated
public class XMPPServiceFactory {

  public static XMPPService getXMPPService() {
    return getFactory().getXMPPService();
  }

  private static IXMPPServiceFactory getFactory() {
    return ServiceFactoryFactory.getFactory(IXMPPServiceFactory.class);
  }
}
