// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.appengine.api.prospectivesearch;

/**
 * Constructs an instance of the Prospective Search service.
 *
 * @deprecated This API has been deprecated.
 */
@Deprecated
public class ProspectiveSearchServiceFactory {
  public static ProspectiveSearchService getProspectiveSearchService() {
    return new ProspectiveSearchServiceImpl();
  }
}
