// Copyright 2007 Google Inc. All rights reserved.

package com.google.appengine.api.urlfetch;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code HTTPResponse} encapsulates the results of a {@code
 * HTTPRequest} made via the {@code URLFetchService}.
 *
 */
public class HTTPResponse implements Serializable {
  static final long serialVersionUID = -4270789523885950851L;

  private final int responseCode;
  private final List<HTTPHeader> headers = new ArrayList<>();
  private final HashMap<String, String> combinedHeadersMap = new LinkedHashMap<>();
  private final byte[] content;
  private final URL finalUrl;

  /**
   * Construct an HTTPResponse object.
   *
   * @param responseCode the HTTP response code
   * @param content the HTTP response content (can be null)
   * @param finalUrl the URL that served the request if redirection was followed (can be null)
   * @param headers the HTTP response headers
   */
  public HTTPResponse(int responseCode, byte[] content, URL finalUrl,
      List<HTTPHeader> headers) {
    this.responseCode = responseCode;
    this.content = content;
    this.finalUrl = finalUrl;
    for (HTTPHeader header : headers) {
      this.headers.add(header);
      String combinedValue = combinedHeadersMap.get(header.getName());
      if (combinedValue == null) {
        combinedHeadersMap.put(header.getName(), header.getValue());
      } else {
        combinedHeadersMap.put(header.getName(), combinedValue + ", " + header.getValue());
      }
    }
  }

  /**
   * Returns the HTTP response code from the request (e.g. 200, 500,
   * etc.).
   */
  public int getResponseCode() {
    return responseCode;
  }

  /**
   * Returns the content of the request, or null if there is no
   * content present (e.g. in a HEAD request).
   */
  public byte[] getContent() {
    return content;
  }

  /**
   * Returns the final URL the content came from if redirects were followed
   * automatically in the request, if different than the input URL; otherwise
   * this will be null.
   */
  public URL getFinalUrl() {
    return finalUrl;
  }

  /**
   * Returns a {@code List} of HTTP response headers that were
   * returned by the remote server. These are not combined for
   * repeated values.
   */
  public List<HTTPHeader> getHeadersUncombined() {
    return Collections.unmodifiableList(headers);
  }

  /**
   * Returns a {@code List} of HTTP response headers that were
   * returned by the remote server. Multi-valued headers are
   * represented as a single {@code HTTPHeader} with comma-separated
   * values.
   */
  public List<HTTPHeader> getHeaders() {
    ArrayList<HTTPHeader> combinedHeaders = new ArrayList<>();
    for (Map.Entry<String, String> entry : combinedHeadersMap.entrySet()) {
      combinedHeaders.add(new HTTPHeader(entry.getKey(), entry.getValue()));
    }
    return Collections.unmodifiableList(combinedHeaders);
  }
}
