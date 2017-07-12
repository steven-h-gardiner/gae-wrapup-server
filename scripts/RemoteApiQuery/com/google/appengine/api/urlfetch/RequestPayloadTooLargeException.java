package com.google.appengine.api.urlfetch;

import java.net.MalformedURLException;

/**
 * {@code RequestPayloadTooLargeException} is thrown when the payload of a {@link URLFetchService}
 * request is too large.
 *
 * <p>This is a subclass of MalformedURLException for backwards compatibility as it is thrown in
 * places where MalformedURLException was thrown previously.
 *
 */
public class RequestPayloadTooLargeException extends MalformedURLException {

  private static final String MESSAGE_FORMAT = "The request to %s exceeded the 10 MiB limit.";

  public RequestPayloadTooLargeException(String url) {
    super(String.format(MESSAGE_FORMAT, url));
  }
}
