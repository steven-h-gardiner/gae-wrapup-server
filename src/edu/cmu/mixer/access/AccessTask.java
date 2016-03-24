package edu.cmu.mixer.access;

public class AccessTask {
  public int numtasks = -1;
  public AccessTask(int numtasks) {
    this.numtasks = numtasks;
  }

  public static org.json.JSONObject getGuideObject(javax.servlet.http.HttpSession session,
						   javax.servlet.http.HttpServletRequest request) throws Exception {
    if (session.getAttribute("guide") != null) {
      org.json.JSONObject guide = new org.json.JSONObject((String) session.getAttribute("guide"));
      //guide.putOpt("literal", (String) session.getAttribute("guide"));
      return guide;
    }

    org.json.JSONObject guide = new org.json.JSONObject();

    guide.putOpt("taskno", 0);
    guide.putOpt("mode", "practice");

    org.json.JSONArray tasks = EventLog.getInstance().getStudyTasks();
    guide.putOpt("tasks", tasks);

    org.json.JSONObject firstTask = tasks.optJSONObject(0);
    guide.putOpt("mintask", firstTask.optString("taskno"));

    org.json.JSONObject lastTask = tasks.optJSONObject(tasks.length() - 1);
    guide.putOpt("maxtask", lastTask.optString("taskno"));

    guide.putOpt("numtasks", tasks.length());
    guide.putOpt("numtasks2", tasks.length() / 2);

    org.json.JSONObject task = tasks.optJSONObject(guide.optInt("taskno", 0));
    guide.putOpt("task", task);
    guide.putOpt("taskid", task.optInt("taskid"));;

    guide.putOpt("sessionid", session.getId());
    guide.putOpt("sessionhash",
		 Integer.toString(Math.abs(session.getId().hashCode()), 32).toUpperCase());

    org.json.JSONArray cookies = new org.json.JSONArray();
    org.json.JSONObject cookiehash = new org.json.JSONObject();
    System.err.println("REQ: " + request);
    System.err.println("COOKIES: " + request.getCookies());
    if (request.getCookies() != null) {
      for (javax.servlet.http.Cookie cookie : request.getCookies()) {
	org.json.JSONObject o = new org.json.JSONObject();
	o.putOpt("name", cookie.getName());
	o.putOpt("value", cookie.getValue());
	o.putOpt("maxage", cookie.getMaxAge());
	
	cookiehash.putOpt(cookie.getName(), cookie.getValue());
	
	cookies.put(o);
      }
    }

    java.util.Calendar cal = new java.util.GregorianCalendar();
    if (cookiehash.has("tablefirst")) {
      guide.putOpt("tfsrc", "cookies");
      guide.putOpt("tablefirst", Boolean.parseBoolean(cookiehash.getString("tablefirst")));
    } else {
      guide.putOpt("tfsrc", "millis");
      guide.putOpt("millis", cal.get(java.util.Calendar.MILLISECOND));
      guide.putOpt("tablefirst", cal.get(java.util.Calendar.MILLISECOND) > 500);
    }
    
    guide.putOpt("cookies", cookies);
    //guide.putOpt("tablefirst", 
    
    guide.putOpt("taskno1", 1+guide.optInt("taskno", 0));
    session.setAttribute("guide", guide.toString());
    guide.putOpt("new", true);
    
    return guide;
  }
    
  public org.json.JSONObject process(javax.servlet.http.HttpSession session,
				     javax.servlet.http.HttpServletRequest request) throws Exception {
    org.json.JSONObject o = new org.json.JSONObject();

    o.putOpt("sessionid", session.getId());
    o.putOpt("hash",Integer.toString(Math.abs(o.optString("sessionid", o.toString()).hashCode()), 32).toUpperCase());
    o.putOpt("sessioninterval", session.getMaxInactiveInterval());

    o.putOpt("taskid", request.getParameter("taskid"));
    o.putOpt("taskno", request.getParameter("taskno"));
    o.putOpt("pageno", request.getParameter("pageno"));
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
    answers.putOpt(o.optString("pageno", o.optString("taskid", null)), o.optString("answer", null));
    
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
    
    org.json.JSONObject orderings = edu.cmu.mixer.access.EventLog.getInstance().drawOrderings(o);
    o.putOpt("orderings", orderings);

    System.err.println("ORDERINGS: " + orderings.toString(2));

    o.putOpt("pid", session.getAttribute("pid"));
    try { o.putOnce("pid", request.getParameter("pid")); } catch (Exception ex) { /* ignore */ }
    session.setAttribute("pid", o.optString("pid"));
  
    com.google.appengine.api.datastore.DatastoreService ds =
      com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();
    
    String bltext = (String) session.getAttribute("blacklist");
    org.json.JSONObject blacklist = (bltext == null) ? null : new org.json.JSONObject(bltext);
    if ((blacklist == null) && o.has("pid")) {
      System.err.println("FORM BLACKLIST");
      blacklist = new org.json.JSONObject();

      //blacklist.put("20", true);

      com.google.appengine.api.datastore.Query blq = 
        new com.google.appengine.api.datastore.Query("Participant");
      blq = blq.setFilter(new com.google.appengine.api.datastore.Query.FilterPredicate("ParticipantID",
                                                                                       com.google.appengine.api.datastore.Query.FilterOperator.EQUAL,
                                                                                       o.optString("pid", "")));
      com.google.appengine.api.datastore.PreparedQuery blpq = ds.prepare(blq);

      for (com.google.appengine.api.datastore.Entity result : blpq.asIterable()) {
        o.putOpt("oldhash", result.getProperty("DataHash"));
      }

      blq = 
        new com.google.appengine.api.datastore.Query("AccessEvent");
      blq = blq.setFilter(com.google.appengine.api.datastore.Query.CompositeFilterOperator.and(new com.google.appengine.api.datastore.Query.FilterPredicate("eventname",
                                                                                                                                                            com.google.appengine.api.datastore.Query.FilterOperator.EQUAL,
                                                                                                                                                            "submitanswer"),
                                                                                               new com.google.appengine.api.datastore.Query.FilterPredicate("hash",
                                                                                                                                                            com.google.appengine.api.datastore.Query.FilterOperator.EQUAL,
                                                                                                                                                            o.optString("oldhash", ""))));

      System.err.println("PREP");
      blpq = ds.prepare(blq);
      System.err.println("QUERY");
      for (com.google.appengine.api.datastore.Entity result : blpq.asIterable()) {
        System.err.println("RESULT: " + result);
        String bltaskid = (String) result.getProperty("taskid");
        com.google.appengine.api.datastore.Key blkey =
          com.google.appengine.api.datastore.KeyFactory.createKey("AccessTask", Long.parseLong(bltaskid));
        com.google.appengine.api.datastore.Entity bltask = ds.get(blkey);
        blacklist.putOpt(bltask.getProperty("taskno").toString(), true);
      }
      
      session.setAttribute("blacklist", blacklist.toString());
    }
    if (blacklist == null) {
      blacklist = new org.json.JSONObject();
    }
    o.putOpt("blacklist", blacklist);
    
    boolean blacklisted = true;

    if (blacklist != null) {
      taskno = -1;
      while (blacklisted) {
        taskno++;
        org.json.JSONArray taskorder = orderings.optJSONArray("taskorder");  
        int pageno = taskorder.optInt(taskno, taskno);
        o.putOpt("pageno", pageno);
        if (blacklist.has(Integer.toString(pageno))) {
          blacklisted = true;
          answers.putOpt(Integer.toString(pageno), "__NA__");
        } else if (answers.has(Integer.toString(pageno))) {
          blacklisted = true;
        } else {
          blacklisted = false;
        }
      }
    } 
    o.putOpt("taskno", taskno);

    int realanswers = 0;
    for (java.util.Iterator i = answers.keys(); i.hasNext(); ) {
      String key = (String) i.next();
      if (! answers.optString(key, "__NA__").equals("__NA__")) {
        realanswers++;
      } 
    }
    
    org.json.JSONArray condorder = orderings.optJSONArray("condorder");
    int condix = realanswers % (condorder.length());
    o.putOpt("condix", condix);
    int condition = condorder.optInt(condix);
    o.putOpt("condition", condition);

    if (taskno >= numtasks) {
      o.putOpt("finished", true);
      return o;
    }

    session.setAttribute("answers", answers.toString());
    
    org.json.JSONArray typeorder = orderings.optJSONArray("typeorder");
    org.json.JSONObject tasksByType = orderings.optJSONObject("tasksbytype");
    if (typeorder != null) {
    String tasktype = typeorder.optString(taskno);
    o.putOpt("tasktype", tasktype);
    //System.err.println("OOOO: " + o.toString(2));
    //System.err.println("TBT : " + tasksByType  );
    org.json.JSONArray tasksOfType = tasksByType.optJSONArray(tasktype);
    o.putOpt("taskcands", tasksOfType);
    //int taskDraw = (int) Math.floor(Math.random() * tasksOfType.length());
    //int staskno = tasksOfType.getJSONObject(taskDraw).optInt("taskno");
    //o.putOpt("pageno.bytasktype", staskno);
    }
    System.err.println("OOOO: " + o.toString(2));
    
  
    com.google.appengine.api.datastore.Entity entity = 
      new com.google.appengine.api.datastore.Entity("AccessTask");
    // default so non-null but never stored to datastore
    
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
