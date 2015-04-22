package edu.cmu.mixer.wrapup;

import org.json.JSONObject;

public class WrapupUser {
  public static java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  public static com.google.appengine.api.users.User getCurrentUser() {
    com.google.appengine.api.users.UserService userService =
      com.google.appengine.api.users.UserServiceFactory.getUserService();
    com.google.appengine.api.users.User user = userService.getCurrentUser();
    return user;
  }

  public static String getTimestamp() {
    return sdf.format(new java.util.Date());
  }

  public static String getUsername() {
    com.google.appengine.api.users.User user = getCurrentUser();
    if (user == null) {
      return null;
    }
    return user.getEmail();
  }
    
  public static boolean canhazAdmin(javax.servlet.http.HttpServletRequest req) throws Exception {
    com.google.appengine.api.users.UserService userService =
      com.google.appengine.api.users.UserServiceFactory.getUserService();
    com.google.appengine.api.users.User user = userService.getCurrentUser();

    boolean auth = true;

    if (true) {        
      auth = auth && (user != null);
      System.err.println(String.format("ID: %s :: %s", auth, user));
      auth = auth && (userService.isUserAdmin());
      System.err.println(String.format("ADMIN: %s", auth));
    }

    String token = req.getParameter("token");
    try {
      if ((token != null) && (! token.equals(""))) {
        System.err.println("TOKEN: " + token);

        if (System.getProperty("adminkey", "").equals(token)) {
          System.err.println("syskey!");
          return true;
        }
        if (System.getProperty("devkey", "").equals(token)) {
          System.err.println("devkey!");
          if (com.google.appengine.api.utils.SystemProperty.environment.value() ==
              com.google.appengine.api.utils.SystemProperty.Environment.Value.Development) {          
            return true;
          }
        }

        com.google.appengine.api.datastore.DatastoreService ds =
          com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();
        
        com.google.appengine.api.datastore.Query query = 
          new com.google.appengine.api.datastore.Query("Key");
        
        com.google.appengine.api.datastore.PreparedQuery pq = 
          ds.prepare(query);
        
        int keyCount = 0;
        for (com.google.appengine.api.datastore.Entity key : pq.asIterable()) {
          if (key != null) {
            keyCount++;
            System.err.println(String.format("KEY: |%s|?=|%s| :: %d",
                                             key.getProperty("key").toString(), 
                                             token, 
                                             key.getProperty("key").toString().compareTo(token)));
            if (key.getProperty("key").toString().equals(token)) {
              System.err.println("MATCH!");
              auth = true;
            }
          }          
        }

        if (keyCount == 0) {
          com.google.appengine.api.datastore.Entity key =
            new com.google.appengine.api.datastore.Entity("Key");

          key.setProperty("key", getTimestamp() + " " + Double.toString(Math.random()));

          ds.put(key);
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }     

    if (! req.isSecure()) {
      if (com.google.appengine.api.utils.SystemProperty.environment.value() !=
          com.google.appengine.api.utils.SystemProperty.Environment.Value.Development) {

        System.err.println("HTTP[^S]!!");
        auth = false;
        // throw new CompromisedKeyException();

      }
    }    

    return auth;
  }
}
