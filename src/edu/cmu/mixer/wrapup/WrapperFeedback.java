package edu.cmu.mixer.wrapup;

import java.util.logging.Level;
import java.util.logging.Logger;


@SuppressWarnings("serial")
public class WrapperFeedback extends javax.servlet.http.HttpServlet {
  private static final Logger logger = Logger.getLogger(WrapperFeedback.class.getName());

  protected static com.google.appengine.api.datastore.DatastoreService ds =
    com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();

  public org.json.JSONObject vote(org.json.JSONArray wrappers, int delta) throws Exception {
    org.json.JSONObject out = new org.json.JSONObject();

    for (int i = 0; i < wrappers.length(); i++) {
      org.json.JSONObject votes = null;
      org.json.JSONObject wrapper = wrappers.optJSONObject(i);
      if (wrapper != null) {
	votes = vote(wrapper, delta);
      }
      org.json.JSONArray parts = wrappers.optJSONArray(i);
      if (parts != null) {
	votes = vote(parts, delta);
      }
      if (votes != null) {
	for (String name : org.json.JSONObject.getNames(votes)) {
	  out.putOpt(name, votes.get(name));
	}
      }
    }
    
    return out;
  }
  
  public org.json.JSONObject vote(org.json.JSONObject wrapper, int delta) throws Exception {
    org.json.JSONObject out = new org.json.JSONObject();

    String voteKey = (delta < 0) ? "downVotes" : "upVotes";
    
    if (wrapper.has("wrapperName")) {
      com.google.appengine.api.datastore.Key wrapperKey = 
	com.google.appengine.api.datastore.KeyFactory.createKey("Wrapper",
								wrapper.getString("wrapperName"));

      com.google.appengine.api.datastore.Entity wrapperEntity = 
	ds.get(wrapperKey);

      long oldValue = wrapperEntity.hasProperty(voteKey) ? (long) wrapperEntity.getProperty(voteKey) : 0;
      
      out.putOpt(voteKey, oldValue + delta);
      wrapperEntity.setProperty(voteKey, out.optInt(voteKey, delta));

      ds.put(wrapperEntity);
    }
    
    return out;
  }
  
  public void doGet(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp)
  throws java.io.IOException {
    this.doPost(req, resp);
  }

  public void doPost(javax.servlet.http.HttpServletRequest req,
                     javax.servlet.http.HttpServletResponse resp)
  throws java.io.IOException {
    String xo = req.getParameter("xo");
    if (xo != null) {
      resp.setHeader("Access-Control-Allow-Origin", "*");
      resp.setHeader("Access-Control-Allow-Methods",
		     "POST, GET, OPTIONS, DELETE");
      resp.setHeader("Access-Control-Max-Age", "3600");
      resp.setHeader("Access-Control-Allow-Headers", "x-requested-with");
    }

    try {    
      //System.err.println("THANKS FOR YOUR FEEDBACK: " + req.getParameterMap().toString());
      String wrapStr = req.getParameter("wrapper");
      System.err.println("WRAPSTR: " + wrapStr);

      String message = req.getParameter("message");
      org.json.JSONObject output = new org.json.JSONObject();

      if (message.equals("contra") || message.equals("pro")) {
      
	int delta = (message.equals("contra")) ? -1 : +1;
	
	output.putOpt("delta", delta);
	try {
	  org.json.JSONObject wrapper = new org.json.JSONObject(wrapStr);
	  output.putOpt("votes", vote(wrapper, delta));
	} catch (Exception ex0) {
	  ex0.printStackTrace(System.err);
	  org.json.JSONArray wrappers = new org.json.JSONArray(wrapStr);
	  output.putOpt("votes", vote(wrappers, delta));
	}
      }
      if (message.equals("wrapper_wanted")) {
	com.google.appengine.api.datastore.Entity reqEntity = 
	  new com.google.appengine.api.datastore.Entity("WrapperRequest");

	reqEntity.setProperty("url", req.getParameter("url"));
	reqEntity.setProperty("timestamp",
			      WrapupUser.getTimestamp());
	reqEntity.setProperty("username",
			      WrapupUser.getUsername());
	
	ds.put(reqEntity);

	output.putOpt("username", reqEntity.getProperty("username"));	
	output.putOpt("reqid", reqEntity.getKey().getId());
      }
      if (message.equals("wrapper_used")) {
	com.google.appengine.api.datastore.Entity entity = 
	  new com.google.appengine.api.datastore.Entity("WrapperAttaboy");

	entity.setProperty("url", req.getParameter("url"));
	entity.setProperty("timestamp",
			   WrapupUser.getTimestamp());
	entity.setProperty("username",
			   WrapupUser.getUsername());
	
	ds.put(entity);

	output.putOpt("username", entity.getProperty("username"));	
	output.putOpt("entid", entity.getKey().getId());
      }
      
      output.putOpt("message", "ack");

      resp.setContentType("application/json");
      resp.getWriter().write(output.toString(2));

    } catch (Exception ex) {
      resp.setContentType("text/plain");
      ex.printStackTrace(resp.getWriter());
      ex.printStackTrace(System.err);
    }
  }  
}
