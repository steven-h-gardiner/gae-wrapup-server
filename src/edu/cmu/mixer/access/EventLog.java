package edu.cmu.mixer.access;

public class EventLog {
  com.google.appengine.api.datastore.DatastoreService ds =
    com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();
  public static java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  private EventLog() {
  }

  private static EventLog instance = null;

  public static EventLog getInstance() {
    if (instance == null) {
      instance = new EventLog();
    }
    return instance;
  }

  public Object getTimestamp() {
    return sdf.format(new java.util.Date());
  }

  public org.json.JSONObject log(org.json.JSONObject event) {
    org.json.JSONObject response = new org.json.JSONObject();

    try {
      com.google.appengine.api.datastore.Entity entity =
        new com.google.appengine.api.datastore.Entity("AccessEvent");
      response.putOpt("eventid0", entity.getKey().getId());
      
      entity.setProperty("timestamp", getTimestamp());
      entity.setProperty("eventname", event.optString("eventname", ""));
      entity.setProperty("target", event.optString("target", ""));
      entity.setProperty("taskno", event.optString("taskno", ""));
      entity.setProperty("taskid", event.optString("taskid", ""));
      entity.setProperty("answer", event.optString("answer", ""));
      entity.setProperty("answercorrect", null);
      entity.setProperty("condition", event.optString("condition", ""));
      entity.setProperty("sessionid", event.optString("sessionid", ""));
      entity.setProperty("hash", event.optString("hash", ""));
      
      ds.put(entity);
      
      response.putOpt("eventid", entity.getKey().getId());
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
    return response;
  }

  private org.json.JSONObject getTaskOrders() throws Exception {
    org.json.JSONObject taskorders = new org.json.JSONObject();
    com.google.appengine.api.datastore.Query query = 
      new com.google.appengine.api.datastore.Query("TaskOrder");
    com.google.appengine.api.datastore.PreparedQuery pq = ds.prepare(query);
    for (com.google.appengine.api.datastore.Entity result : pq.asIterable()) {
      String key = result.getKey().toString();
      taskorders.putOpt(key, new org.json.JSONArray(result.getProperty("ordering").toString()));
    }

    if (taskorders.length() == 0) {
      taskorders.putOpt("taskthru", new org.json.JSONArray());
    }
    
    return taskorders;
  }

  private org.json.JSONObject getCondOrders() throws Exception {
    org.json.JSONObject condorders = new org.json.JSONObject();
    com.google.appengine.api.datastore.Query query = 
      new com.google.appengine.api.datastore.Query("CondOrder");
    com.google.appengine.api.datastore.PreparedQuery pq = ds.prepare(query);
    for (com.google.appengine.api.datastore.Entity result : pq.asIterable()) {
      String key = result.getKey().toString();
      condorders.putOpt(key, new org.json.JSONArray(result.getProperty("ordering").toString()));
    }

    if (condorders.length() == 0) {
      condorders.putOpt("condthru", new org.json.JSONArray("[0,1,3]"));
    }
    
    return condorders;
  }
  
  private org.json.JSONObject orderingCache = null;
  public org.json.JSONObject drawOrderings(org.json.JSONObject info) throws Exception {
    if (orderingCache == null) {
      orderingCache = new org.json.JSONObject();

      orderingCache.putOpt("taskorders", this.getTaskOrders());
      orderingCache.putOpt("condorders", this.getCondOrders());

    }
    System.err.println("ORDERING CACHE: " + orderingCache.toString(2));

    if (orderingCache.has(info.optString("sessionid"))) {
      return orderingCache.optJSONObject(info.optString("sessionid"));
    }

    org.json.JSONObject taskorders = orderingCache.optJSONObject("taskorders");
    org.json.JSONObject condorders = orderingCache.optJSONObject("condorders");
    
    org.json.JSONObject counts = new org.json.JSONObject();
    for (String orderid : org.json.JSONObject.getNames(taskorders)) {
      counts.putOpt(orderid, 0);
    }
    for (String orderid : org.json.JSONObject.getNames(condorders)) {
      counts.putOpt(orderid, 0);
    }
    /*
      taskorders.forEach(function(to) { counts[to.id] = 0; }
      condorders.forEach(function(co) { counts[co.id] = 0; }      
     */

    com.google.appengine.api.datastore.Query query = 
      new com.google.appengine.api.datastore.Query("OrderStatus");
    com.google.appengine.api.datastore.PreparedQuery pq = ds.prepare(query);
    for (com.google.appengine.api.datastore.Entity result : pq.asIterable()) {
      System.err.println("STATUS: " + result);
      
      String taskkey = (String) result.getProperty("taskorderid");
      if (taskkey != null) {
	counts.putOpt(taskkey, 1+counts.optInt(taskkey, 0));
      }
      String condkey = (String) result.getProperty("condorderid");
      if (condkey != null) {
	counts.putOpt(condkey, 1+counts.optInt(condkey, 0));
      }
    }

    org.json.JSONObject orderings = new org.json.JSONObject();

    int mintaskcount = Integer.MAX_VALUE;
    String mintaskid = null;
    int mincondcount = Integer.MAX_VALUE;
    String mincondid = null;
    for (String orderid : org.json.JSONObject.getNames(taskorders)) {
      int ordercount = counts.optInt(orderid, Integer.MAX_VALUE);
      if (ordercount < mintaskcount) {
	mintaskid = orderid;
	mintaskcount = ordercount;
      }
    }
    for (String orderid : org.json.JSONObject.getNames(condorders)) {
      int ordercount = counts.optInt(orderid, Integer.MAX_VALUE);
      if (ordercount < mincondcount) {
	mincondid = orderid;
	mincondcount = ordercount;
      }
    }
    /* 
      taskorders.forEach(function(to) { 
        if (counts[to.id] < mintaskcount) { 
          mintaskid = to.id;
          mintaskcount = counts[to.id];
        }
      }
      condorders.forEach(function(co) { 
        if (counts[co.id] < mincondcount) { 
          mincondid = co.id;
          mincondcount = counts[co.id];
        }
      }
     */

    orderings.putOpt("taskorderid", mintaskid);
    orderings.putOpt("taskordercount", mintaskcount);
    orderings.putOpt("taskorder", taskorders.optJSONArray(mintaskid));
    orderings.putOpt("condorderid", mincondid);
    orderings.putOpt("condordercount", mincondcount);
    orderings.putOpt("condorder", condorders.optJSONArray(mincondid));
    
    // INSERT RESERVATION FOR mintaskid AS taskrez
    // INSERT RESERVATION FOR mincondid AS condrez
    com.google.appengine.api.datastore.Entity taskrez =
      new com.google.appengine.api.datastore.Entity("OrderStatus");
    taskrez.setProperty("taskorderid", mintaskid);
    taskrez.setProperty("type", "reservation");
    taskrez.setProperty("sessionid", info.optString("sessionid"));
    taskrez.setProperty("sessionhash", info.optString("hash"));
    taskrez.setProperty("reztime", getTimestamp());
    ds.put(taskrez);
    orderings.putOpt("taskstatusid", taskrez.getKey().getId());
    
    com.google.appengine.api.datastore.Entity condrez =
      new com.google.appengine.api.datastore.Entity("OrderStatus");
    condrez.setProperty("condorderid", mincondid);
    condrez.setProperty("type", "reservation");
    condrez.setProperty("sessionid", info.optString("sessionid"));
    condrez.setProperty("sessionhash", info.optString("hash"));
    condrez.setProperty("reztime", getTimestamp());
    ds.put(condrez);
    orderings.putOpt("condstatusid", condrez.getKey().getId());
    
    // ENQUEUE UNRESERVER FOR taskrez.getKey().getID()
    // ENQUEUE UNRESERVER FOR condrez.getKey().getID();
    com.google.appengine.api.taskqueue.Queue queue = 
      com.google.appengine.api.taskqueue.QueueFactory.getDefaultQueue();
    queue.add(com.google.appengine.api.taskqueue.TaskOptions.Builder.withPayload(new Unreserver(taskrez.getKey().getId())).countdownMillis(1000*60*60*6));
    queue.add(com.google.appengine.api.taskqueue.TaskOptions.Builder.withPayload(new Unreserver(condrez.getKey().getId())).countdownMillis(1000*60*60*6));    

    // orderings["taskorder"] = orderingCache.taskorders[mintaskid];
    // orderings["condorder"] = orderingCache.condorders[mincondid];
    
    orderingCache.putOpt(info.optString("sessionid"), orderings);
    return orderings;
  }

  public static class Unreserver implements com.google.appengine.api.taskqueue.DeferredTask {
    public long rezid;
    public Unreserver(long rezid) {
      this.rezid = rezid;
    }

    public void run() {
      try {
	System.err.println("REMOVE " + rezid);
	
        com.google.appengine.api.datastore.DatastoreService ds =
          com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();
	com.google.appengine.api.datastore.Entity status = ds.get(com.google.appengine.api.datastore.KeyFactory.createKey("OrderStatus", rezid));
	if (! status.getProperty("type").toString().equals("reservation")) {
	  return;
	}
	System.err.println("REMOVING: " + status.toString());
	ds.delete(status.getKey());	
      } catch (Exception ex) {
	ex.printStackTrace(System.err);
      }
    }
  }

}
