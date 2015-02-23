package edu.cmu.mixer.access;

import org.json.JSONObject;

public class AddOrdering extends javax.servlet.http.HttpServlet {
  public void doGet(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {
    this.doPost(req, resp);
  }

  public void doPost(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {

    boolean auth = false;
    try {
      auth = edu.cmu.mixer.wrapup.WrapupUser.canhazAdmin(req);
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

      spec0.putOpt("kind", req.getParameter("kind"));
      org.json.JSONArray list = null;
      if (req.getParameterValues("item") != null) {
	for (String itemparam : req.getParameterValues("item")) {
	  if (list == null) {
	    list = new org.json.JSONArray();
	  }
	  list.put(itemparam);
	}
      }
      spec0.putOpt("list", list);
      spec0.putOpt("json", req.getParameter("json"));
      if (spec0.has("json")) {
	spec0.putOpt("list", new org.json.JSONArray(spec0.optString("json", "[]")));
      }

      if (! spec0.has("kind")) {
	resp.sendError(resp.SC_FORBIDDEN, "missing kind");
	return;
      }
      if (! spec0.has("list")) {
	resp.sendError(resp.SC_FORBIDDEN, "missing list");
	return;
      }

      com.google.appengine.api.datastore.Entity ordering =
	new com.google.appengine.api.datastore.Entity(spec0.optString("kind"));
      ordering.setProperty("ordering", spec0.getJSONArray("list").toString());
      ds.put(ordering);      
      
      resp.setContentType("text/plain");
      resp.getWriter().write(ordering.toString());      
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
  }

  protected static com.google.appengine.api.datastore.DatastoreService ds =
    com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();
}
