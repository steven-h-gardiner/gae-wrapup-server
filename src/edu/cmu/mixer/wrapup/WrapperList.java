package edu.cmu.mixer.wrapup;

public class WrapperList {
  org.json.JSONObject output = new org.json.JSONObject();
  org.json.JSONArray dataset = new org.json.JSONArray();
  org.json.JSONArray columns = new org.json.JSONArray();
  org.json.JSONArray columnTitles = new org.json.JSONArray();
  
  public WrapperList(org.json.JSONObject spec) throws Exception {
    output.putOpt("dataset", dataset);
    output.putOpt("columns", columns);
    
    if (spec.optBoolean("mockup", false)) {
      for (int j = 0; j < spec.optInt("mockup_columns", 3); j++) {
	columnTitles.put("COL" + j);
      }
      for (int i = 0; i < spec.optInt("mockup_rows", 100); i++) {
	org.json.JSONArray row = new org.json.JSONArray();
	for (int j = 0; j < spec.optInt("mockup_columns", 3); j++) {
	  org.json.JSONObject cell = new org.json.JSONObject();
	  
	  cell.put("text","ROW" + i +";COL" + j + ";r" + Math.random());
	  cell.put("href","http://www.google.com");

	  row.put(cell);
	}
	dataset.put(row);
      }
      return;
    }

    com.google.appengine.api.datastore.DatastoreService ds =
      com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();

    com.google.appengine.api.datastore.FetchOptions fo = 
      com.google.appengine.api.datastore.FetchOptions.Builder.withDefaults();
    com.google.appengine.api.datastore.Query query =
      new com.google.appengine.api.datastore.Query("Wrapper");
    
    query = query.addSort("url",
			  com.google.appengine.api.datastore.Query.SortDirection.ASCENDING); 
    query = query.addSort("diameter",
			  com.google.appengine.api.datastore.Query.SortDirection.DESCENDING); 
    query = query.addSort("timestamp",
			  com.google.appengine.api.datastore.Query.SortDirection.DESCENDING); 

    String prevurl = "";
    
    com.google.appengine.api.datastore.PreparedQuery pq =
      ds.prepare(query);
    columnTitles.put("url");
    columnTitles.put("diameter");
    columnTitles.put("timestamp");
    for (com.google.appengine.api.datastore.Entity result : pq.asIterable(fo)) {
      String url = result.getProperty("url").toString();

      if (spec.optBoolean("activeOnly", true)) {                  
	if (url.equals(prevurl)) {
	  continue;
	}
	prevurl = url;
        if (((Long) result.getProperty("diameter")) == 0) {
          continue;
        }
      }
      
      org.json.JSONArray row = new org.json.JSONArray();
      org.json.JSONObject urlCell = new org.json.JSONObject();
      urlCell.putOpt("href",url);
      urlCell.putOpt("text",url.substring(0,Math.min(url.length(),60)));
      row.put(urlCell);
      row.put(result.getProperty("diameter").toString());
      row.put(result.getProperty("timestamp").toString());
      dataset.put(row);

    }    
  }

  public org.json.JSONObject getJSONObject() throws Exception {
    for (int j = 0; j < columnTitles.length(); j++) {
      org.json.JSONObject colObj = new org.json.JSONObject();
      colObj.putOpt("title", columnTitles.optString(j));
      columns.put(colObj);
    }
    return output;
  }
}
