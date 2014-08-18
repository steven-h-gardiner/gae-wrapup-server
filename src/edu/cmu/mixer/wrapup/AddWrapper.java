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

    try {
      org.json.JSONObject spec0 = new org.json.JSONObject();
      spec0.putOpt("wrapper", req.getParameter("wrapper"));
      spec0.putOpt("url", req.getParameter("url"));
      spec0.putOpt("signature", req.getParameter("signature"));
      
      if (spec0.has("wrapper")) {
        addWrapper(spec0);
      }

      for (String jsontxt : req.getParameterValues("json")) {
        org.json.JSONObject spec = new org.json.JSONObject(jsontxt);

        if (spec.has("wrapper")) {
          addWrapper(spec);
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
  }

  protected static com.google.appengine.api.datastore.DatastoreService ds =
    com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();

  public static void addWrapper(org.json.JSONObject spec) {
    System.err.println("ADDWRAP: " + spec.toString());

    com.google.appengine.api.datastore.Entity wrapper =
      new com.google.appengine.api.datastore.Entity("Wrapper");
    
    if (spec.opt("wrapper") != null) {
      wrapper.setProperty("wrapper", spec.opt("wrapper").toString());
    }
    if (spec.opt("signature") != null) {
      wrapper.setProperty("signature", spec.opt("signature").toString());
    }
    wrapper.setProperty("url", spec.optString("url", null));

    ds.put(wrapper);
  }

}
