/*
 *
 * To run:
 * Compile with "javac -cp '.:appengine-api.jar:appengine-remote-api.jar'  InsertEntity.java"
 * Run with "java -cp '.:appengine-api.jar:appengine-remote-api.jar'  InsertEntity"
 *
 */

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import java.io.FileWriter;

//import static org.apache.commons.lang3.StringEscapeUtils.unescapeJava;

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
      query.addSort("timestamp",
                            com.google.appengine.api.datastore.Query.SortDirection.ASCENDING);

      Filter dateFilter = new FilterPredicate("timestamp", FilterOperator.GREATER_THAN_OR_EQUAL, "2017");
      query.setFilter(dateFilter);
      
      PreparedQuery pq = ds.prepare(query);
      FetchOptions fo = FetchOptions.Builder.withDefaults();

      String path = "out.csv";
      FileWriter writer = new FileWriter(path);

      writer.append("answer|condition|eventname|hash|target|taskid|taskno|timestamp\n");
      
      int i = 0;
      
      for (Entity e : pq.asIterable(fo)){
        String answer = (String)e.getProperty("answer");
        String noEscape = answer.replace("\n", "\\n");
        noEscape = noEscape.replace("\r", "\\r");
        noEscape = noEscape.replace("\t", "\\t");
        //noEscape = noEscape.replace(",","\,");
        String condition = (String)e.getProperty("condition");
        String eventname = (String)e.getProperty("eventname");
        String hash = (String)e.getProperty("hash");
        String target = (String)e.getProperty("target");
        String taskid = (String)e.getProperty("taskid");
        String taskno = (String)e.getProperty("taskno");
        String timestamp = (String)e.getProperty("timestamp");

        writer.append(noEscape);
        writer.append("|");
        writer.append(condition);
        writer.append("|");
        writer.append(eventname);
        writer.append("|");
        writer.append(hash);
        writer.append("|");
        writer.append(target);
        writer.append("|");
        writer.append(taskid);
        writer.append("|");
        writer.append(taskno);
        writer.append("|");
        writer.append(timestamp);
        writer.append("\n");
        
        
        i++;
        }
      
      System.out.print("Number of records = ");
      System.out.println(i);
      
      writer.flush();
      writer.close();
    }
    finally {
      installer.uninstall();
    }
  }
}
