package edu.cmu.mixer.wrapup;

import org.json.JSONObject;

public class HelloWorld extends javax.servlet.http.HttpServlet {
  public void doGet(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {
    this.doPost(req, resp);
  }

  public void doPost(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {

    try {
      org.json.JSONObject json = new JSONObject();
      
      json.putOpt("now", new java.util.Date().toString());

      resp.setContentType("application/json");
      resp.getWriter().write(json.toString(2));
    } catch (Exception ex) {
      resp.setContentType("text/plain");
      ex.printStackTrace(resp.getWriter());
    }
  }
}
