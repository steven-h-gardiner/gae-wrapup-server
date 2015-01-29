package edu.cmu.mixer.wrapup;

import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class AddWrapper extends javax.servlet.http.HttpServlet {

  private static final String[] properties =
    {"url", "signature", "wrapper", "timestamp", "grade", "isauto", "addtime"};

  public void doGet(javax.servlet.http.HttpServletRequest req,
                    HttpServletResponse resp)
  throws java.io.IOException {
    this.doPost(req, resp);
  }

  public void doPost(javax.servlet.http.HttpServletRequest req,
                     HttpServletResponse resp)
  throws java.io.IOException {
    boolean auth = false;
    try {
      auth = WrapupUser.canhazAdmin(req);
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
      auth = false;
    }

    System.err.println("NUH-UH");
    if (! auth) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "not for you");
      return;
    }

    try {
      JSONObject jsonobj = new JSONObject();
      String value = null;
      for (String property : properties) {
        value = req.getParameter(property);
        if (value != null) {
          jsonobj.putOpt(property, value);
        }
      }
      jsonobj.putOnce("addtime", WrapupUser.getTimestamp());

      if (jsonobj.has("wrapper")) {
        jsonobj.putOpt("ack", addWrapper(jsonobj));
      }

      if (req.getParameterValues("json") != null) {
	org.json.JSONArray acks = new org.json.JSONArray();
        for (String jsontxt : req.getParameterValues("json")) {
          JSONObject spec = new JSONObject(jsontxt);

          if (spec.has("wrapper")) {
            acks.put(addWrapper(spec));
          }
        }
	jsonobj.putOpt("acks", acks);
      }

      resp.setContentType("application/json");
      resp.getWriter().write(jsonobj.toString(2));
      resp.getWriter().write("\n");
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
  }

  protected static com.google.appengine.api.datastore.DatastoreService ds =
    com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();

  public static JSONObject addWrapper(JSONObject jsonobj)
  throws Exception {
    System.err.println("ADDWRAP: " + jsonobj.toString());

    /** Add wrapper to DataStore. **/
    com.google.appengine.api.datastore.Entity wrapper =
      new com.google.appengine.api.datastore.Entity("Wrapper"); // Wrapper
    for (String property : JSONObject.getNames(jsonobj)) {
      if (property.equals("wrapper")) {
        wrapper.setProperty(property,
                            new com.google.appengine.api.datastore.Text(jsonobj.get(property).toString()));
        continue;
      }
      wrapper.setProperty(property, jsonobj.get(property));
    }
    ds.put(wrapper);

    /** Give ack. **/
    org.json.JSONObject ack = new JSONObject();
    ack.putOpt("wrapper", wrapper);
    return ack;
  }

}
