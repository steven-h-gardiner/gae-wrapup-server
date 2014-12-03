package edu.cmu.mixer.util;

import org.json.JSONObject;

public class ServletUtil {
  public static String getMD5Sum(byte[] digest) {
    StringBuffer md5 = new StringBuffer();
    for (byte b : digest) {
      String s = "00" + Integer.toHexString(new Byte(b).intValue());
      md5.append(s.substring(s.length() - 2));
    }
    return md5.toString();
  }

  public static String getMD5Sum(String s) throws Exception {
    java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(s.getBytes());
    java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
    java.security.DigestInputStream dis = new java.security.DigestInputStream(bais, md);

    byte[] buf = new byte[1024];
    int len = dis.read(buf);
    while (len > 0) {
      len = dis.read(buf);
    }
    dis.close();
    bais.close();
    return getMD5Sum(md.digest());
  }
  
  public static org.json.JSONObject req2json(javax.servlet.http.HttpServletRequest req) throws Exception {
    JSONObject json = new JSONObject();

    java.util.Enumeration names = req.getParameterNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement().toString();
      String[] values = req.getParameterValues(name);
      
      if (values.length > 1) {
	json.putOpt(name, new org.json.JSONArray(values));
      } else {
	json.putOpt(name, values[0]);
      }
    }

    return json;
  }
}
