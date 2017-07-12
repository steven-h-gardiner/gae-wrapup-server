// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.appengine.api.rdbms;

import com.google.cloud.sql.jdbc.Driver;
import com.google.cloud.sql.jdbc.internal.ConnectionProperty;
import com.google.cloud.sql.jdbc.internal.Url;

import com.mysql.jdbc.GoogleNonRegisteringDriver;
import com.mysql.jdbc.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppEngineDriver implements java.sql.Driver {

  static final String USE_GOOGLE_MYSQL_DRIVER_FOR_GOOGLE_RDBMS =
      "appengine.jdbc.useGoogleMysqlDriverForGoogleRdbms";

  private static final String LEGACY_RDBMS_PREFIX = "jdbc:google:rdbms://";

  private static final Logger LOG = Logger.getLogger(AppEngineDriver.class.getName());

  private final java.sql.Driver delegateDriver;

  static {
    registerDriver();
  }

  private static void registerDriver() {
    try {
      DriverManager.registerDriver(new AppEngineDriver());
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Unable to register AppEngineDriver automatically.", e);
    }
  }

  private static class AppEngineGoogleMySqlDriver extends GoogleNonRegisteringDriver {
    public AppEngineGoogleMySqlDriver() throws SQLException {}

    @Override
    protected String getAllowedPrefix() {
      return LEGACY_RDBMS_PREFIX;
    }

    @Override
    public java.sql.Connection connect(String url, Properties info) throws SQLException {
      if (url != null && StringUtils.startsWithIgnoreCase(url, getAllowedPrefix())) {
        Url parsedUrl = Url.create(url, null);
        String user = parsedUrl.getProperties().get(ConnectionProperty.USER.key());
        if (user == null || user.isEmpty()) {
          info.put(ConnectionProperty.USER.key(), "root");
        }
        return super.connect(url, info);
      }
      return null;
    }
  }

  private static class AppEngineRdbmsDriver extends Driver {
    public AppEngineRdbmsDriver() {
      super(new RdbmsApiProxyClientFactory());
    }
  }

  public AppEngineDriver() {
    this.delegateDriver = createDelegateDriver();
  }

  @Override
  public boolean acceptsURL(String url) throws SQLException {
    return this.delegateDriver.acceptsURL(url);
  }

  @Override
  public Connection connect(String urlStr, Properties info) throws SQLException {
    return this.delegateDriver.connect(urlStr, info);
  }

  @Override
  public final int getMajorVersion() {
    return this.delegateDriver.getMajorVersion();
  }

  @Override
  public final int getMinorVersion() {
    return this.delegateDriver.getMinorVersion();
  }

  @Override
  public final DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
      throws SQLException {
    return this.delegateDriver.getPropertyInfo(url, info);
  }

  @Override
  public final boolean jdbcCompliant() {
    return this.delegateDriver.jdbcCompliant();
  }

  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return this.delegateDriver.getParentLogger();
  }

  private static java.sql.Driver createDelegateDriver() {
    java.sql.Driver driver = null;
    try {
      if (Boolean.getBoolean(USE_GOOGLE_MYSQL_DRIVER_FOR_GOOGLE_RDBMS)) {
        driver = new AppEngineGoogleMySqlDriver();
      } else {
        driver = new AppEngineRdbmsDriver();
      }
    } catch (SQLException e) {
      if (Boolean.getBoolean(USE_GOOGLE_MYSQL_DRIVER_FOR_GOOGLE_RDBMS)) {
        LOG.log(Level.SEVERE, "Unable to create AppEngineGoogleMySqlDriver.", e);
      } else {
        LOG.log(Level.SEVERE, "Unable to create AppEngineRdbmsDriver.", e);
      }
    }
    return driver;
  }
}
