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

  public static java.util.regex.Pattern rangePatt =
    java.util.regex.Pattern.compile("(\\p{Alpha}+)_(\\d+)(\\-|x)(\\d+)\\.?");
    
  public static org.json.JSONObject grokFile(String filepath) throws org.json.JSONException {
    org.json.JSONObject grok = new org.json.JSONObject();
    
    grok.putOpt("filepath", filepath);

    String[] path = filepath.split("/");
    grok.putOpt("path", new org.json.JSONArray(path));

    String filename = path[-1+path.length];
    grok.putOpt("filename", filename);
    
    org.json.JSONArray parts = new org.json.JSONArray(filename.split("\\."));

    if (parts.length() > 1) {
      String extension = parts.remove(-1+parts.length()).toString();
      grok.putOpt("extension", extension);
    }
    String basename = "";
    for (int i = 0; i < parts.length(); i++) {
      basename = basename + parts.optString(i) + ".";
    }
    java.util.regex.Matcher m = rangePatt.matcher(basename);
    if (m.matches()) {
      grok.putOpt("basename", m.group(1));
      grok.putOpt("lb", Integer.parseInt(m.group(2)));
      String divider = m.group(3);
      if ("x".equals(divider)) {
	grok.putOpt("width", Integer.parseInt(m.group(4)));
      } else {
	grok.putOpt("ub", Integer.parseInt(m.group(4)));
	grok.putOpt("width", grok.optInt("ub") - grok.optInt("lb"));
      }
    } else {
      grok.putOpt("basename", basename.substring(0,-1+basename.length()));
    }

    return grok;
  }
}
