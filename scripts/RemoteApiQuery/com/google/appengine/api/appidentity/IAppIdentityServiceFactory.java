// Copyright 2012 Google Inc. All rights reserved.
package com.google.appengine.api.appidentity;

/**
 * Creates new instances of the App Identity service.
 *
 */
public interface IAppIdentityServiceFactory {
   AppIdentityService getAppIdentityService();
}
