package com.google.appengine.api.mail;

import javax.annotation.Nullable;
import javax.mail.internet.MimeMessage;

/**
 * The {@code BounceNotification} object represents an incoming bounce
 * notification.
 *
 */
public final class BounceNotification {
  /**
   * The {@code BounceNotification.Details} class describes either the original
   * message that caused a bounce, or the notification message describing the
   * bounce.
   */
  public final static class Details {
    private final String from;
    private final String to;
    private final String cc;
    private final String bcc;
    private final String subject;
    private final String text;

    private Details(@Nullable String from, @Nullable String to, @Nullable String cc,
                    @Nullable String bcc, @Nullable String subject, @Nullable String text) {
      this.from = from;
      this.to = to;
      this.cc = cc;
      this.bcc = bcc;
      this.subject = subject;
      this.text = text;
    }

    /**
     * @return the 'from' field for this detail item.
     */
    @Nullable
    public String getFrom() {
      return from;
    }

    /**
     * @return the 'to' field for this detail item.
     */
    @Nullable
    public String getTo() {
      return to;
    }

    /**
     * @return the 'cc' field for this detail item.
     */
    @Nullable
    public String getCc() {
      return cc;
    }

    /**
     * @return the 'bcc' field for this detail item.
     */
    @Nullable
    public String getBcc() {
      return bcc;
    }

    /**
     * @return the 'subject' field for this detail item.
     */
    @Nullable
    public String getSubject() {
      return subject;
    }

    /**
     * @return the 'text' field for this detail item.
     */
    @Nullable
    public String getText() {
      return text;
    }
  }

  static class DetailsBuilder {
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String text;

    public Details build() {
      return new Details(from, to, cc, bcc, subject, text);
    }

    public DetailsBuilder withFrom(@Nullable String from) {
      this.from = from;
      return this;
    }

    public DetailsBuilder withTo(@Nullable String to) {
      this.to = to;
      return this;
    }

    public DetailsBuilder withCc(@Nullable String cc) {
      this.cc = cc;
      return this;
    }

    public DetailsBuilder withBcc(@Nullable String bcc) {
      this.bcc = bcc;
      return this;
    }

    public DetailsBuilder withSubject(@Nullable String subject) {
      this.subject = subject;
      return this;
    }

    public DetailsBuilder withText(@Nullable String text) {
      this.text = text;
      return this;
    }
  }

  static class BounceNotificationBuilder {
    public BounceNotification build() {
      return new BounceNotification(rawMessage, original, notification);
    }

    public BounceNotificationBuilder withRawMessage(MimeMessage rawMessage) {
      this.rawMessage = rawMessage;
      return this;
    }

    public BounceNotificationBuilder withOriginal(BounceNotification.Details original) {
      this.original = original;
      return this;
    }

    public BounceNotificationBuilder withNotification(BounceNotification.Details notification) {
      this.notification = notification;
      return this;
    }

    private MimeMessage rawMessage;
    private BounceNotification.Details original;
    private BounceNotification.Details notification;
  }

  BounceNotification(@Nullable MimeMessage rawMessage, @Nullable Details original,
                     @Nullable Details notification) {
    this.rawMessage = rawMessage;
    this.original = original;
    this.notification = notification;
  }

  /**
   * @return the original MIME message that caused the bounce.
   */
  @Nullable
  public final MimeMessage getRawMessage() {
    return rawMessage;
  }

  /**
   * @return the parsed Details of the original message.
   */
  @Nullable
  public final Details getOriginal() {
    return original;
  }

  /**
   * @return the parsed Details describing the bounce.
   */
  @Nullable
  public final Details getNotification() {
    return notification;
  }

  private final MimeMessage rawMessage;
  private final Details original;
  private final Details notification;
}
