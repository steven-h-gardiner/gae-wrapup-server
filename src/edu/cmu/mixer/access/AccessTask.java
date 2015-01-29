package edu.cmu.mixer.access;

public class AccessTask {
  public int numtasks = -1;
  public AccessTask(int numtasks) {
    this.numtasks = numtasks;
  }

  public org.json.JSONObject process(javax.servlet.http.HttpSession session,
				     javax.servlet.http.HttpServletRequest request) throws Exception {
    org.json.JSONObject o = new org.json.JSONObject();

    o.putOpt("sessionid", session.getId());
    o.putOpt("hash",Integer.toString(Math.abs(o.optString("sessionid", o.toString()).hashCode()), 32).toUpperCase());
    o.putOpt("sessioninterval", session.getMaxInactiveInterval());

    o.putOpt("taskid", request.getParameter("taskid"));
    o.putOpt("taskno", request.getParameter("taskno"));
    o.putOpt("answer", request.getParameter("answer"));
    o.putOpt("condition", request.getParameter("condition"));
    
    o.putOpt("answers", (session.getAttribute("answers") == null) ? null : new org.json.JSONObject((String) session.getAttribute("answers")));
    //o.putOpt("sanswers", session.getAttribute("answers"));
    o.putOnce("answers", (request.getParameter("answers") == null) ? null : new org.json.JSONObject(request.getParameter("answers")));
    //o.putOpt("ranswers", request.getParameter("answers"));
    
    org.json.JSONObject answers = o.optJSONObject("answers");
    if (answers == null) {
      answers = new org.json.JSONObject();
    }
    answers.putOpt(o.optString("taskid", null), o.optString("answer", null));
    
    o.putOpt("eventname", "submitanswer");
    if (o.has("taskid") &&  (! o.optString("taskid").equals(""))) {
      o.putOpt("response", edu.cmu.mixer.access.EventLog.getInstance().log(o));
    }
    o.remove("eventname");
    o.remove("taskid");
    o.remove("taskno");
    o.remove("answer");

    o.putOpt("answers", answers);
    int taskno = answers.length();
    o.putOpt("taskno", taskno);
    
    session.setAttribute("answers", answers.toString());

    org.json.JSONObject orderings = edu.cmu.mixer.access.EventLog.getInstance().drawOrderings(o);
    o.putOpt("orderings", orderings);

    if (taskno >= numtasks) {
      o.putOpt("finished", true);
      return o;
    }
    
  
    org.json.JSONArray condorder = orderings.optJSONArray("condorder");
    int condix = taskno % (condorder.length());
    o.putOpt("condix", condix);
    int condition = condorder.optInt(condix);
    o.putOpt("condition", condition);
    
    org.json.JSONArray taskorder = orderings.optJSONArray("taskorder");  
    int pageno = taskorder.optInt(taskno, taskno);
    o.putOpt("pageno", pageno);

    com.google.appengine.api.datastore.Entity entity = 
      new com.google.appengine.api.datastore.Entity("AccessTask");
    // default so non-null but never stored to datastore
    
    com.google.appengine.api.datastore.DatastoreService ds =
      com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();
    com.google.appengine.api.datastore.Query query = 
      new com.google.appengine.api.datastore.Query("AccessTask");
    com.google.appengine.api.datastore.PreparedQuery pq = null;
    /*
    if (o.has("url")) {
      query = query.setFilter(new com.google.appengine.api.datastore.Query.FilterPredicate("url",
											   com.google.appengine.api.datastore.Query.FilterOperator.EQUAL,
											   o.optString("url")));
      
      pq = ds.prepare(query);
      for (com.google.appengine.api.datastore.Entity result : pq.asIterable()) {
	entity = result;
      }
    }
    */
    
    if (o.optInt("pageno", -1) >= 0) {
      
      
      query = query.setFilter(new com.google.appengine.api.datastore.Query.FilterPredicate("taskno",
											   com.google.appengine.api.datastore.Query.FilterOperator.EQUAL,
											   o.optInt("pageno", -1)));
      
      pq = ds.prepare(query);
    }
    if (pq != null) {
      for (com.google.appengine.api.datastore.Entity result : pq.asIterable()) {
	entity = result;
	o.putOpt("taskid", entity.getKey().getId());
      }
    }

    o.putOpt("title", entity.getProperty("title"));
    o.putOpt("question", entity.getProperty("question"));
    o.putOpt("url", entity.getProperty("url"));

    o.putOpt("eventname", "viewtask");
    o.putOpt("response", edu.cmu.mixer.access.EventLog.getInstance().log(o));
    o.remove("eventname");
    
    System.err.println("OOO: " + o.toString(2));
    
    return o;
  }
}
