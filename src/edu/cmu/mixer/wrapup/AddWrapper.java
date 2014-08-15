package edu.cmu.mixer.wrapup;

import org.json.JSONObject;

public class AddWrapper extends javax.servlet.http.HttpServlet {

  public void doGet(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {
    this.doPost(req, resp);
  }

  public void doPost(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {

    boolean auth = false;
    try {
      auth = WrapupUser.canhazAdmin(req);
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
      auth = false;
    }
    
    System.err.println("NUH-UH");
    if (! auth) {
      resp.sendError(resp.SC_FORBIDDEN, "not for you");
      return;
    }
    

  }

}
