package com.google.appengine.api.datastore;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

/**
 * Proxy around System.getenv() to enable testing and prevent errors in situations where the
 * security manager does not allow access to environment variables.
 */
class EnvProxy {

  private static ImmutableMap<String, String> envOverride;

  private EnvProxy() {}

  /**
   * Updates to {@code envOverride} made after calling this method will not be
   * reflected in calls to {@link #getenv(String)}.
   */
  static synchronized void setEnvOverrideForTest(Map<String, String> envOverride) {
    EnvProxy.envOverride = ImmutableMap.copyOf(envOverride);
  }

  static synchronized void clearEnvOverrideForTest() {
    envOverride = null;
  }

  static String getenv(String name) {
    synchronized (EnvProxy.class) {
      if (envOverride != null) {
        return envOverride.get(name);
      }
    }
    try {
      return System.getenv(name);
    } catch (SecurityException e) {
      return null;
    }
  }
}
