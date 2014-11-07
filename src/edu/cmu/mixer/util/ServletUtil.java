package edu.cmu.mixer.util;

import org.json.JSONObject;

public class ServletUtil {
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
