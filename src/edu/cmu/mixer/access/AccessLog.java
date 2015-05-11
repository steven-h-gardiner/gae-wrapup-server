package edu.cmu.mixer.access;

import org.json.JSONObject;

public class AccessLog extends javax.servlet.http.HttpServlet {
  public void doGet(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {
    this.doPost(req, resp);
  }

  public void doPost(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {

    String xo = req.getParameter("xo");
    if (xo != null) {
      resp.setHeader("Access-Control-Allow-Origin", "*");
      resp.setHeader("Access-Control-Allow-Methods",
		     "POST, GET, OPTIONS, DELETE");
      resp.setHeader("Access-Control-Max-Age", "3600");
      resp.setHeader("Access-Control-Allow-Headers", "x-requested-with");
    }

    try {
      org.json.JSONObject json = new JSONObject();
      
      json.putOpt("eventname", req.getParameter("eventname"));
      json.putOpt("taskurl", req.getParameter("taskurl"));

      java.net.URL taskurl = new java.net.URL(json.optString("taskurl", "http://www.google.com"));
      json.putOpt("taskquery", taskurl.getQuery());
      json.putOpt("hash", json.optString("taskquery", "").substring(5));

      json.putOpt("keystrokes0", (req.getParameter("keystrokes") != null));
      org.json.JSONArray keystrokes = null;
      if (req.getParameter("keystrokes") != null) {
	keystrokes = new org.json.JSONArray(req.getParameter("keystrokes"));
      }
      
      json.putOpt("keystrokes", keystrokes);
      
      resp.setContentType("application/json");
      resp.getWriter().write(json.toString(2));
      resp.getWriter().write("\n");

      System.err.println("NOW: " + json.toString(2));
    } catch (Exception ex) {
      resp.setContentType("text/plain");
      ex.printStackTrace(resp.getWriter());
    }
  }
}
