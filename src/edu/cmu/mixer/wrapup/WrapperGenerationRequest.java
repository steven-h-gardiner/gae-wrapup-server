package edu.cmu.mixer.wrapup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class WrapperGenerationRequest extends HttpServlet {

  private static final Logger logger = Logger.getLogger(WrapperGenerationRequest.class.getName());

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {
    this.doPost(req, resp);
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {
    String url = req.getParameter("url");

    logger.log(Level.INFO,
               String.format("Get a request with param: {url:%s}", url));

    try {
      logger.log(Level.INFO, "Recording a wrapper generation request for url: " + url);
      recordWrapperGenerationRequest(url);
    } catch (Exception e) {
      resp.setContentType("text/plain");
      e.printStackTrace(resp.getWriter());
      return;
    }

    resp.setContentType("text/plain");
    resp.getWriter().println("Inserted a new request!");

    logger.log(Level.INFO, "Inserted a new Request!");
  }

  protected static DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

  public static final String DATA_STORE_KIND = "RequestedURL";

  public static final String COLUMN_URL = "url";
  public static final String COLUMN_TIMES_REQUESTED = "times_requested";
  public static final String COLUMN_TIMESTAMP_REQUESTED = "timestamp_requested";
  public static final String COLUMN_REQUEST_EXPORTED = "request_exported";

  public static final SimpleDateFormat dateFormat =
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  public static void recordWrapperGenerationRequest(String url) throws Exception {
    if (url == null) {
      throw new NullPointerException("url is null");
    }

    Key key = KeyFactory.createKey(DATA_STORE_KIND, url);
    Entity entity = new Entity(key);

    try {
      entity = ds.get(key);
    } catch (EntityNotFoundException e) {
      logger.log(Level.WARNING,
                 String.format("Record with url %s doens't exisit", url));

      /* Initialize the initial request. */
      entity.setProperty(COLUMN_URL, url);
      entity.setProperty(COLUMN_TIMES_REQUESTED, 0);
    }

    entity.setProperty(COLUMN_TIMES_REQUESTED,
                       new Integer(entity.getProperty(COLUMN_TIMES_REQUESTED).toString()) + 1);
    entity.setProperty(COLUMN_TIMESTAMP_REQUESTED, dateFormat.format(new Date()));
    entity.setProperty(COLUMN_REQUEST_EXPORTED, false);

    ds.put(entity);

    logger.log(Level.INFO, "Update the record as: " + entity);
  }

}
