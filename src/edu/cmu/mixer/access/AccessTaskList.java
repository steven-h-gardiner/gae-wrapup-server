package edu.cmu.mixer.access;

public class AccessTaskList  extends javax.servlet.http.HttpServlet {
  public void doGet(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {
    this.doPost(req, resp);
  }

  public void doPost(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {

    try {
      com.google.appengine.api.datastore.DatastoreService ds =
	com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();
      com.google.appengine.api.datastore.Query query = 
	new com.google.appengine.api.datastore.Query("AccessTask");

      query = query.setFilter(new com.google.appengine.api.datastore.Query.FilterPredicate("representative",
											   com.google.appengine.api.datastore.Query.FilterOperator.NOT_EQUAL,
											   null));
      query = query.setFilter(new com.google.appengine.api.datastore.Query.FilterPredicate("representative",
											   com.google.appengine.api.datastore.Query.FilterOperator.NOT_EQUAL,
											   ""));

      query = query.setFilter(new com.google.appengine.api.datastore.Query.FilterPredicate("taskno",
											   com.google.appengine.api.datastore.Query.FilterOperator.GREATER_THAN,
											   -1));

      query = query.addSort("taskno");
      
      com.google.appengine.api.datastore.PreparedQuery pq = ds.prepare(query);
    
      org.json.JSONObject spec = edu.cmu.mixer.util.ServletUtil.req2json(req);

      String format = spec.optString("format", "csv");
      if (format.equals("csv")) {
	resp.setContentType("text/csv");
	for (com.google.appengine.api.datastore.Entity task : pq.asIterable()) {
	  resp.getWriter().write(new Long(task.getKey().getId()).toString());
	  resp.getWriter().write(",");
	  resp.getWriter().write(task.getProperty("representative").toString());
	  resp.getWriter().write(",");
	  resp.getWriter().write(task.getProperty("taskno").toString());
	  resp.getWriter().write(",");
	  resp.getWriter().write(task.getProperty("url").toString());
	  resp.getWriter().write("\n");
	}
      }

    } catch (Exception ex) {
      resp.setContentType("text/plain");
      ex.printStackTrace(resp.getWriter());
    }

  }
}
