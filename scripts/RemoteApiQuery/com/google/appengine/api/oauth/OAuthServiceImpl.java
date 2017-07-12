// Copyright 2010 Google Inc. All rights reserved.

package com.google.appengine.api.oauth;

import com.google.appengine.api.users.User;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.UserServicePb.GetOAuthUserRequest;
import com.google.apphosting.api.UserServicePb.GetOAuthUserResponse;
import com.google.apphosting.api.UserServicePb.UserServiceError;
import com.google.io.protocol.ProtocolMessage;
import java.util.Arrays;
import java.util.Objects;

/**
 * Implementation of {@link OAuthService}.
 *
 */
final class OAuthServiceImpl implements OAuthService {
  static final String GET_OAUTH_USER_RESPONSE_KEY =
      "com.google.appengine.api.oauth.OAuthService.get_oauth_user_response";
  static final String GET_OAUTH_USER_SCOPE_KEY =
      "com.google.appengine.api.oauth.OAuthService.get_oauth_user_scope";
  static final String REQUEST_WRITER_PERMISSION_KEY =
      "com.google.appengine.api.oauth.OAuthService.request_writer_permission";

  private static final String PACKAGE = "user";
  private static final String GET_OAUTH_USER_METHOD = "GetOAuthUser";

  @Override
  public User getCurrentUser() throws OAuthRequestException {
    return getCurrentUser((String[]) null);
  }

  @Override
  public User getCurrentUser(String scope) throws OAuthRequestException {
    String[] scopes = {scope};
    return getCurrentUser(scopes);
  }

  @Override
  public User getCurrentUser(String... scopes) throws OAuthRequestException {
    GetOAuthUserResponse response = getGetOAuthUserResponse(scopes);
    return new User(response.getEmail(), response.getAuthDomain(),
        response.getUserId());
  }

  @Override
  public boolean isUserAdmin() throws OAuthRequestException {
    return isUserAdmin((String[]) null);
  }

  @Override
  public boolean isUserAdmin(String scope) throws OAuthRequestException {
    String[] scopes = {scope};
    return isUserAdmin(scopes);
  }

  @Override
  public boolean isUserAdmin(String... scopes) throws OAuthRequestException {
    return getGetOAuthUserResponse(scopes).isIsAdmin();
  }

  @Override
  public String getOAuthConsumerKey() throws OAuthRequestException {
    throw new OAuthRequestException("Two-legged OAuth1 is not supported any more");
  }

  @Override
  public String getClientId(String scope) throws OAuthRequestException {
    String[] scopes = {scope};
    return getClientId(scopes);
  }

  @Override
  public String getClientId(String... scopes) throws OAuthRequestException {
    GetOAuthUserResponse response = getGetOAuthUserResponse(scopes);
    return response.getClientId();
  }

  @Override
  public String[] getAuthorizedScopes(String... scopes) throws OAuthRequestException {
    GetOAuthUserResponse response = getGetOAuthUserResponse(scopes);
    return response.scopess().toArray(new String[response.scopesSize()]);
  }

  private GetOAuthUserResponse getGetOAuthUserResponse(String[] scopes)
      throws OAuthRequestException {
    ApiProxy.Environment environment = ApiProxy.getCurrentEnvironment();
    if (environment == null) {
      throw new IllegalStateException(
          "Operation not allowed in a thread that is neither the original request thread "
              + "nor a thread created by ThreadManager");
    }
    GetOAuthUserResponse response = (GetOAuthUserResponse)
        environment.getAttributes().get(GET_OAUTH_USER_RESPONSE_KEY);
    String scopesKey = "[]";
    if (scopes != null && scopes.length > 0) {
      String[] scopesCopy = scopes.clone();
      Arrays.sort(scopesCopy);
      scopesKey = Arrays.toString(scopesCopy);
    }
    String lastScopesKey = (String) environment.getAttributes().get(GET_OAUTH_USER_SCOPE_KEY);
    if (response == null || !Objects.equals(lastScopesKey, scopesKey)) {
      GetOAuthUserRequest request = new GetOAuthUserRequest();
      if (scopes != null) {
        for (String scope : scopes) {
          request.addScopes(scope);
        }
      }
      Boolean requestWriterPermission = (Boolean) environment.getAttributes().get(
          REQUEST_WRITER_PERMISSION_KEY);
      if (requestWriterPermission != null && requestWriterPermission) {
        request.setRequestWriterPermission(true);
      }
      byte[] responseBytes = makeSyncCall(GET_OAUTH_USER_METHOD, request);
      response = new GetOAuthUserResponse();
      boolean parsed = response.mergeFrom(responseBytes);
      if (!parsed || !response.isInitialized()) {
        throw new OAuthServiceFailureException("Could not parse GetOAuthUserResponse");
      }
      environment.getAttributes().put(GET_OAUTH_USER_RESPONSE_KEY, response);
      environment.getAttributes().put(GET_OAUTH_USER_SCOPE_KEY, scopesKey);
    }
    return response;
  }

  private byte[] makeSyncCall(String methodName, ProtocolMessage request)
      throws OAuthRequestException {
    byte[] responseBytes;
    try {
      byte[] requestBytes = request.toByteArray();
      responseBytes = ApiProxy.makeSyncCall(PACKAGE, methodName, requestBytes);
    } catch (ApiProxy.ApplicationException ex) {
      UserServiceError.ErrorCode errorCode =
          UserServiceError.ErrorCode.valueOf(ex.getApplicationError());
      switch (errorCode) {
        case NOT_ALLOWED:
        case OAUTH_INVALID_REQUEST:
          throw new InvalidOAuthParametersException(ex.getErrorDetail());
        case OAUTH_INVALID_TOKEN:
          throw new InvalidOAuthTokenException(ex.getErrorDetail());
        case OAUTH_ERROR:
        default:
          throw new OAuthServiceFailureException(ex.getErrorDetail());
      }
    }

    return responseBytes;
  }

}
