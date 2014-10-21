package edu.cmu.mixer.wrapup;

import org.json.JSONObject;
import org.json.JSONArray;

public class WrapupQuery extends javax.servlet.http.HttpServlet {
  public void doGet(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {
    this.doPost(req, resp);
  }

  public void doPost(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {

    String xo = req.getParameter("xo");
    if (xo != null) {
      resp.setHeader("Access-Control-Allow-Origin", "*");
      resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
      resp.setHeader("Access-Control-Max-Age", "3600");
      resp.setHeader("Access-Control-Allow-Headers", "x-requested-with");
    }
    
    try {
      String callback = req.getParameter("callback");     

      org.json.JSONArray wrappers = new JSONArray();
      org.json.JSONObject json = new JSONObject();
      
      json.putOpt("url", req.getParameter("url"));
      json.putOpt("wrappers", wrappers);

      System.err.println("QUERY: " + json.toString());

      if (req.getParameter("signature") != null) {
        org.json.JSONArray bysig = queryBySignature(req.getParameter("signature"));
        for (int i = 0; i < bysig.length(); i++) {
          wrappers.put(bysig.opt(i));
        }
      }

      if (req.getParameter("url") != null) {
        org.json.JSONArray byurl = queryByURL(req.getParameter("url"));
        for (int i = 0; i < byurl.length(); i++) {
          wrappers.put(byurl.opt(i));
        }
        
      }

      if (wrappers.length() == 0) {
        resp.setStatus(resp.SC_NO_CONTENT);
      }
      

      if (callback == null) {
        resp.setContentType("application/json");
        resp.getWriter().write(json.toString(2));
      } else {
        resp.setContentType("application/javascript");
        resp.getWriter().write(callback);
        resp.getWriter().write("(");
        resp.getWriter().write(json.toString(2));
        resp.getWriter().write(");\n");
      }
    } catch (Exception ex) {
      resp.setContentType("text/plain");
      ex.printStackTrace(resp.getWriter());
    }
  }

  protected static com.google.appengine.api.datastore.DatastoreService ds =
    com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();

  public static org.json.JSONArray queryBySignature(String signature) {
    // PLACEHOLDER; NOT YET IMPLEMENTED
    return new org.json.JSONArray();
  }
  public static org.json.JSONArray queryByURL(String url) {
    org.json.JSONArray results = new JSONArray();
            
    com.google.appengine.api.datastore.Query query = 
      new com.google.appengine.api.datastore.Query("Wrapper");
    com.google.appengine.api.datastore.Query.FilterPredicate filter =
      new com.google.appengine.api.datastore.Query.FilterPredicate("url",
                                                                   com.google.appengine.api.datastore.Query.FilterOperator.EQUAL,
                                                                   url);
    query = query.setFilter(filter);

    com.google.appengine.api.datastore.PreparedQuery pq = 
      ds.prepare(query);
    for (com.google.appengine.api.datastore.Entity result : pq.asIterable()) {
      
      String wrapperString = ((com.google.appengine.api.datastore.Text) result.getProperty("wrapper")).getValue();
      try {
        results.put(new org.json.JSONArray(wrapperString));
      } catch (Exception ex) {
        try {
          results.put(new org.json.JSONObject(wrapperString));
        } catch (Exception ex2) {
          ex2.printStackTrace(System.err);
        }
      }

    }
    
    return results;
  }
}
