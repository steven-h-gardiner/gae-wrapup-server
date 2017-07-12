// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.appengine.api.channel;

import com.google.appengine.api.utils.HttpRequestParser;
import java.io.IOException;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;

/**
 * {@code ChannelPresenceParser} encapsulates the use of the {@code javax.mail} package to parse an
 * incoming {@code multipart/form-data} HTTP request and to convert it to a {@link Presence} object.
 *
 * @deprecated This API has been <a
 *     href="https://cloud.google.com/appengine/docs/deprecations/channel">deprecated</a>.
 */
@Deprecated
class ChannelPresenceParser extends HttpRequestParser {
  static ChannelPresence parsePresence(HttpServletRequest request) throws IOException {
    try {
      MimeMultipart multipart = parseMultipartRequest(request);
      if (multipart == null) {
        throw new IllegalArgumentException(
            "No arguments provided in request.");
      }

      boolean isConnected;
      if (request.getRequestURI().endsWith("/channel/connected/")) {
        isConnected = true;
      } else if (request.getRequestURI().endsWith("/channel/disconnected/")) {
        isConnected = false;
      } else {
        throw new IllegalArgumentException(
            "Can't determine the type of channel presence from the path: " +
            request.getRequestURI());
      }

      int parts = multipart.getCount();
      for (int i = 0; i < parts; i++) {
        BodyPart part = multipart.getBodyPart(i);
        if ("from".equals(getFieldName(part))) {
          return new ChannelPresence(isConnected, getTextContent(part));
        }
      }

      throw new IllegalArgumentException(
          "Can't determine clientId from request body: " + multipart);
    } catch (MessagingException ex) {
      IOException ex2 = new IOException("Could not parse incoming request.");
      ex2.initCause(ex);
      throw ex2;
    }
  }
}
