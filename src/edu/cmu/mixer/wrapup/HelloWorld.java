package edu.cmu.mixer.wrapup;

import org.json.JSONObject;

public class HelloWorld extends javax.servlet.http.HttpServlet {
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
      
      json.putOpt("now", new java.util.Date().toString());

      resp.setContentType("application/json");
      resp.getWriter().write(json.toString(2));
      resp.getWriter().write("\n");
    } catch (Exception ex) {
      resp.setContentType("text/plain");
      ex.printStackTrace(resp.getWriter());
    }
  }
}
