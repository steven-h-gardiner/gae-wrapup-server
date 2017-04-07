//package com.example.appengine.remote;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import java.io.FileWriter;

import java.io.IOException;

public class RemoteApiQuery
{
  public static void main(String[] args) throws IOException
  {
    RemoteApiOptions options = new RemoteApiOptions()
      .server("gae-wrapup-server.appspot.com", 443)
      .useApplicationDefaultCredential();

    RemoteApiInstaller installer = new RemoteApiInstaller();
    installer.install(options);

  
    try {
      DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
      Query query = new Query("AccessEvent");
      query = query.addSort("timestamp",
                            com.google.appengine.api.datastore.Query.SortDirection.ASCENDING);
      PreparedQuery pq = ds.prepare(query);
      FetchOptions fo = FetchOptions.Builder.withDefaults();
      int i = 0;
      for (Entity e : pq.asIterable(fo)){
        i++;
      }
      System.out.println(i);
    }
    finally {
      installer.uninstall();
    }
  }
}
