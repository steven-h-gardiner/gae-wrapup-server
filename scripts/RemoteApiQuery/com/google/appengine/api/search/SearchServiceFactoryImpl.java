// Copyright 2012 Google Inc. All rights reserved.

package com.google.appengine.api.search;

import com.google.appengine.api.NamespaceManager;

/**
 * An factory that creates default implementation of {@link SearchService}.
 *
 */
final class SearchServiceFactoryImpl implements ISearchServiceFactory {

  static SearchApiHelper apiHelper = new SearchApiHelper();

  /**
   * Returns an instance of the {@link SearchService}.  The instance
   * will exist either in the namespace set on the {@link
   * NamespaceManager}, or, if none was set, in an empty namespace.
   *
   * @return the default implementation of {@link SearchService}.
   *
   * @VisibleForTesting
   */
  static SearchService getSearchService(SearchApiHelper helper) {
    return new SearchServiceImpl(helper == null ? apiHelper : helper,
        SearchServiceConfig.newBuilder().setNamespace(NamespaceManager.get()).build());
  }

  @Override
  public final SearchService getSearchService(String namespace) {
    return getSearchService(SearchServiceConfig.newBuilder().setNamespace(namespace).build());
  }

  @Override
  public SearchService getSearchService(SearchServiceConfig config) {
    return new SearchServiceImpl(apiHelper, config);
  }
}
