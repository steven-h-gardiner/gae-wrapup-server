package edu.cmu.mixer.util;

import org.json.JSONObject;

import au.com.bytecode.opencsv.CSVWriter;

public class BulkExport extends javax.servlet.http.HttpServlet {
  public void doGet(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {
    com.google.appengine.api.users.UserService userService =
      com.google.appengine.api.users.UserServiceFactory.getUserService();
    com.google.appengine.api.users.User user = userService.getCurrentUser();

    boolean auth = false;
    try {
      auth = edu.cmu.mixer.wrapup.WrapupUser.canhazAdmin(req);
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
      auth = false;
    }

    System.err.println("NUH-UH");
    if (! auth) {
      resp.sendError(resp.SC_FORBIDDEN, "badbad");
      return;
    }

    String type = req.getParameter("type");
    String format = req.getParameter("format");    
    String header = req.getParameter("header");
    boolean writeHeader = (header != null) ? Boolean.parseBoolean(header) : false; 
    String useCacheParam = req.getParameter("useCache");
    boolean useCache = (useCacheParam != null) ? Boolean.parseBoolean(useCacheParam) : false;

    String url = req.getRequestURL().toString();

    try {
      org.json.JSONObject grok = ServletUtil.grokFile(url);
      System.err.println("GROK: " + grok.toString(2));
    
      if (type == null) {
        if (grok.has("basename")) {
          type = grok.optString("basename");
        }
      }
      if (format == null) {
        if (grok.has("extension")) {
          format = grok.optString("extension");
        }
      }
      if (format == null) {
        format = "json";
      }
    } catch (Exception ex) {
      throw new Error(ex);
    }


    String cacheKeyString = String.format("%s/%s?%s", "bulk", type, req.getQueryString());
    com.google.appengine.api.datastore.Key cacheKey = 
      com.google.appengine.api.datastore.KeyFactory.createKey("BulkCache", cacheKeyString);
    String deferParam = req.getParameter("defer");
    boolean defer = (deferParam != null) ? Boolean.parseBoolean(deferParam) : false;

    org.json.JSONObject jo = new org.json.JSONObject();
    try {
      jo.putOpt("limit", req.getParameter("limit"));
      jo.putOpt("offset", req.getParameter("offset"));
    } catch (Exception ex) {
      //ignore
    }

    int limit = Integer.parseInt(jo.optString("limit", "1000"));
    int offset = Integer.parseInt(jo.optString("offset", "0"));    

    try {
      String servletPath = req.getServletPath();


      if (useCache) {
        com.google.appengine.api.datastore.DatastoreService ds =
          com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();

        System.err.println("CHECK CACHE: " + cacheKey);
        String blobkeystr = null;
        try {
          com.google.appengine.api.datastore.Entity cacheItem = 
            ds.get(cacheKey);
          System.err.println("CACHE HIT: " + cacheItem);
          blobkeystr = cacheItem.getProperty("blobkey").toString();
        } catch (Exception ex) {
          System.err.println("CACHE MISS");
        }
        if (blobkeystr != null) {
          com.google.appengine.api.blobstore.BlobKey blobkey = new com.google.appengine.api.blobstore.BlobKey(blobkeystr);
          com.google.appengine.api.blobstore.BlobstoreService blobstoreService =
            com.google.appengine.api.blobstore.BlobstoreServiceFactory.getBlobstoreService();
          blobstoreService.serve(blobkey, resp);
          return;
        }
      }

      java.io.PrintWriter output = resp.getWriter();
      
      CacheLoader cl = new CacheLoader();
      cl.useCache = useCache;
      cl.type = type;
      cl.offset = offset;
      cl.limit = limit;
      cl.format = format;
      cl.output = output;
      cl.url = url;
      cl.servletPath = servletPath;      
      cl.cacheKey = cacheKey;
      cl.writeHeader = writeHeader;

      if (defer) {
        cl.output = null;
        com.google.appengine.api.taskqueue.Queue queue = 
          com.google.appengine.api.taskqueue.QueueFactory.getDefaultQueue();
        queue.add(com.google.appengine.api.taskqueue.TaskOptions.Builder.withPayload(cl));

        resp.setContentType("text/plain");
        resp.getWriter().println("QUEUED!  CHECK LATER FOR |" + cacheKeyString + "|");
      } else {
        cl.resp = resp;
        cl.run();
      }

      return;
      
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }

  }

  public class CacheLoader implements com.google.appengine.api.taskqueue.DeferredTask {
    public boolean useCache;
    String type = null;
    int offset = 0;
    int limit = -1;
    String format = null;
    java.io.PrintWriter output = null;
    String servletPath = null;
    String url = null;
    com.google.appengine.api.datastore.Key cacheKey = null;
    javax.servlet.http.HttpServletResponse resp = null;
    boolean writeHeader = false;

    public void run() {
      try {
        int recordnum = 0;

        org.json.JSONArray o = new org.json.JSONArray();
                
        com.google.appengine.api.datastore.DatastoreService ds =
          com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();

        com.google.appengine.api.datastore.Query query = 
          new com.google.appengine.api.datastore.Query(type);
        if (type.equals("Upload")) {
          query = query.addSort("when",
                                com.google.appengine.api.datastore.Query.SortDirection.DESCENDING);
        }
        if (type.equals("Response")) {
          query = query.addSort("when",
                                com.google.appengine.api.datastore.Query.SortDirection.ASCENDING);
        }
        if (type.equals("Request")) {
          query = query.addSort("when",
                                com.google.appengine.api.datastore.Query.SortDirection.ASCENDING);
        }
        com.google.appengine.api.datastore.PreparedQuery pq = 
          ds.prepare(query);
        com.google.appengine.api.datastore.FetchOptions fo = 
          com.google.appengine.api.datastore.FetchOptions.Builder.withDefaults().limit(limit).offset(offset);
        for (com.google.appengine.api.datastore.Entity entity : pq.asIterable(fo)) {
          recordnum++;
          org.json.JSONObject item = new org.json.JSONObject();
          o.put(item);
          item.put("id", entity.getKey().getId());
          item.put("key", entity.getKey().toString());
          item.put("recordnum", (recordnum + offset));
          for (java.util.Map.Entry<String,Object> entry : entity.getProperties().entrySet()) {
            Object value = entry.getValue();
            if (value instanceof com.google.appengine.api.datastore.Text) {
              item.put(entry.getKey().toString(), ((com.google.appengine.api.datastore.Text) value).getValue());
            } else {
              item.put(entry.getKey().toString(), value);
            }
          }
        }
        
        //useCache = useCache && (o.length() >= limit);
        
        com.google.appengine.api.files.FileService fileService = null;
        com.google.appengine.api.files.AppEngineFile file = null;
        com.google.appengine.api.files.FileWriteChannel writeChannel = null;
        
        if (format.equals("json")) {
          if (resp != null) {
            resp.setContentType("application/json");
          }
          if (useCache) {
            String filename = String.format("%s_%d-%d.%s", type, offset, offset+limit-1, format);
            System.err.println("FILENAME: " + filename);
            fileService = com.google.appengine.api.files.FileServiceFactory.getFileService();
            file = fileService.createNewBlobFile("application/json", filename);
            boolean lock = true;
            writeChannel = fileService.openWriteChannel(file, lock);
            
            java.io.PrintWriter pw = new java.io.PrintWriter(java.nio.channels.Channels.newOutputStream(writeChannel));
            output = new java.io.PrintWriter(new TeeWriter(output, pw));
          }
          output.println(o.toString(2));

          if (useCache) {
            writeChannel.closeFinally();
            com.google.appengine.api.datastore.Entity cacheItem = 
              new com.google.appengine.api.datastore.Entity(cacheKey);
            cacheItem.setProperty("blobkey", fileService.getBlobKey(file).getKeyString());
            cacheItem.setProperty("type", type);
            cacheItem.setProperty("offset", offset);
            ds.put(cacheItem);        
          }

          return;
        }
        if (format.equals("csv")) {
          if (resp != null) {
            resp.setContentType("text/csv");
          }
          //resp.getWriter().println(o.toString(2));
          
          java.util.SortedMap<String,String> columns = 
            new java.util.TreeMap<String,String>();
          
          if (type.equals("Response")) {
            columns.put("assignmentId", "");
            columns.put("grade", "");
            columns.put("surveytime", "");
            columns.put("guesswho", "");
            columns.put("url", "");
            columns.put("recordnum", "");
          }              
          if (type.equals("Request")) {
            columns.put("respid", "");
            columns.put("consent", "");
            columns.put("url", "");
            columns.put("urlmd5", "");
            columns.put("taskurl", "");
          }              
          if (type.equals("Task")) {
            columns.put("url", "");
            columns.put("urlmd5", "");
            columns.put("genus", "");
            columns.put("when", "");
            columns.put("recordnum", "");
            columns.put("schema", "");
          }              
          
          for (int i = 0; i < o.length(); i++) {
            org.json.JSONObject row = o.optJSONObject(i);
            org.json.JSONArray cols = row.names();
            for (int j = 0; j < cols.length(); j++) {            
              String key = cols.optString(j);
              columns.put(key, row.opt(key).toString());
            }
          }        
        

          if (useCache) {
            String filename = String.format("%s_%d-%d.%s", type, offset, offset+limit-1, format);
            System.err.println("FILENAME: " + filename);
            fileService = com.google.appengine.api.files.FileServiceFactory.getFileService();
            file = fileService.createNewBlobFile("text/csv", filename);
            boolean lock = true;
            writeChannel = fileService.openWriteChannel(file, lock);
            
            java.io.PrintWriter pw = new java.io.PrintWriter(java.nio.channels.Channels.newOutputStream(writeChannel));
            output = new java.io.PrintWriter(new TeeWriter(output, pw));
          }
          CSVWriter csv = new CSVWriter(output);
          String[] contents = new String[columns.keySet().size()];

          if (writeHeader) {
            int j = 0;
            for (String key : columns.keySet()) {            
              Object value = key;
              contents[j] = (value != null) ? value.toString() : null;
              j++;
            }
            csv.writeNext(contents);
          }
          
          for (int i = 0; i < o.length(); i++) {
            org.json.JSONObject row = o.optJSONObject(i);
            int j = 0;
            for (String key : columns.keySet()) {            
              Object value = row.opt(key);
              contents[j] = (value != null) ? value.toString() : null;
              j++;
            }
            
            csv.writeNext(contents);
          }
          csv.close();
        }

        if (useCache) {
          writeChannel.closeFinally();
          com.google.appengine.api.datastore.Entity cacheItem = 
            new com.google.appengine.api.datastore.Entity(cacheKey);
          cacheItem.setProperty("blobkey", fileService.getBlobKey(file).getKeyString());
          cacheItem.setProperty("type", type);
          cacheItem.setProperty("offset", offset);
          ds.put(cacheItem);        
        }
      } catch (Exception ex) {
        ex.printStackTrace(System.err);
      }
    }
  }
}
