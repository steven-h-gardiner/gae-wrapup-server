// Copyright 2007 Google Inc. All rights reserved.

package com.google.appengine.api.users;

import java.util.Set;

/**
 * The UserService provides information useful for forcing a user to
 * log in or out, and retrieving information about the user who is
 * currently logged-in.
 *
 */
public interface UserService {
  /**
   * Returns a URL that can be used to display a login page to the user.
   *
   * @param destinationURL where the user will be redirected after
   *                       they log in.
   * @return The URL that will present a login prompt.
   *
   * @throws IllegalArgumentException If the destinationURL is not valid.
   */
  public String createLoginURL(String destinationURL);

  /**
   * Returns a URL that can be used to display a login page to the user.
   *
   * @param destinationURL where the user will be redirected after
   *                       they log in.
   * @param authDomain not used.
   * @return The URL that will present a login prompt.
   *
   * @throws IllegalArgumentException If the destinationURL is not valid.
   */
  public String createLoginURL(String destinationURL,
                               String authDomain);

  /**
   * Returns a URL that can be used to redirect the user to a third party for federated login.
   *
   * @param destinationURL where the user will be redirected after they log in.
   * @param authDomain not used.
   * @param federatedIdentity Setting this parameter to a non-null value will throw an
   *     IllegalArgumentException. OpenID 2.0 support has been decommissioned. Please see
   *     https://cloud.google.com/appengine/docs/deprecations/open_id for details.
   * @param attributesRequest not used.
   * @return The URL that will present a login prompt.
   * @throws IllegalArgumentException If the destinationURL is not valid or if federatedIdentity is
   *     not null
   * @deprecated Use of federatedIdentity is no longer supported. Please change calls to a method
   *     that does not take the parameter.
   */
  @Deprecated
  public String createLoginURL(
      String destinationURL,
      String authDomain,
      String federatedIdentity,
      Set<String> attributesRequest);

  /**
   * Returns a URL that can be used to log the current user out of this app.
   *
   * @param destinationURL where the user will be redirected after
   *                       they log out.
   * @return The URL that will log the user out.
   *
   * @throws IllegalArgumentException If the destinationURL is not valid.
   */
  public String createLogoutURL(String destinationURL);

  /**
   * Returns a URL that can be used to log the current user out of this app.
   *
   * @param destinationURL where the user will be redirected after
   *                       they log out.
   * @param authDomain not used.
   * @return The URL that will log the user out.
   *
   * @throws IllegalArgumentException If the destinationURL is not valid.
   */
  public String createLogoutURL(String destinationURL,
                                String authDomain);

  /**
   * Returns true if there is a user logged in, false otherwise.
   */
  public boolean isUserLoggedIn();

  /**
   * Returns true if the user making this request is an admin for this
   * application, false otherwise.
   *
   * @throws IllegalStateException If the current user is not logged in.
   */
  public boolean isUserAdmin();

  /**
   * If the user is logged in, this method will return a {@code User} that
   * contains information about them.
   *
   * Note that repeated calls may not necessarily return the same
   * {@code User} object.
   *
   * @return User if the user is logged in, null otherwise.
   */
  public User getCurrentUser();
}
