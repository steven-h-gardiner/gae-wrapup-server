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
      
      ds.put(entity);
      
      response.putOpt("eventid", entity.getKey().getId());
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
    return response;
  }
}
