package edu.cmu.mixer.access;

public class EventLog {
  com.google.appengine.api.datastore.DatastoreService ds =
    com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();
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
    return new java.util.Date();
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
    
    org.json.JSONObject counts = new org.json.JSONObject();
    /*
      taskorders.forEach(function(to) { counts[to.id] = 0; }
      condorders.forEach(function(co) { counts[co.id] = 0; }      
     */

    com.google.appengine.api.datastore.Query query = 
      new com.google.appengine.api.datastore.Query("OrderStatus");
    com.google.appengine.api.datastore.PreparedQuery pq = ds.prepare(query);
    for (com.google.appengine.api.datastore.Entity result : pq.asIterable()) {
      String taskkey = result.getProperty("taskorderid").toString();
      if (taskkey != null) {
	counts.putOpt(taskkey, 1+counts.optInt(taskkey, 0));
      }
      String condkey = result.getProperty("condorderid").toString();
      if (condkey != null) {
	counts.putOpt(condkey, 1+counts.optInt(condkey, 0));
      }
    }

    int mintaskcount = Integer.MAX_VALUE;
    String mintaskid = null;
    int mincondcount = Integer.MAX_VALUE;
    String mincondid = null;
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

    // INSERT RESERVATION FOR mintaskid AS taskrez
    // INSERT RESERVATION FOR mincondid AS condrez

    // ENQUEUE UNRESERVER FOR taskrez.getKey().getID()
    // ENQUEUE UNRESERVER FOR condrez.getKey().getID();
    
    org.json.JSONObject orderings = new org.json.JSONObject();
    // orderings["taskorder"] = orderingCache.taskorders[mintaskid];
    // orderings["condorder"] = orderingCache.condorders[mincondid];
    
    orderingCache.putOpt(info.optString("sessionid"), orderings);
    return orderings;
  }

  public class Unreserver implements com.google.appengine.api.taskqueue.DeferredTask {
    public long rezid;
    public Unreserver(long rezid) {
      this.rezid = rezid;
    }

    public void run() {
      try {
        com.google.appengine.api.datastore.DatastoreService ds =
          com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();
	ds.delete(com.google.appengine.api.datastore.KeyFactory.createKey("OrderStatus", rezid));	
      } catch (Exception ex) {
	ex.printStackTrace(System.err);
      }
    }
  }
}
