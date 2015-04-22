<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          version="1.2">
  <jsp:directive.page contentType="text/html;charset=UTF-8" language="java" />
  <jsp:scriptlet>
<![CDATA[

  org.json.JSONObject o = new org.json.JSONObject();
  o.putOpt("numtasks", request.getParameter("numtasks"));


  int numtasks = Integer.parseInt(o.optString("numtasks", "0"));
  edu.cmu.mixer.access.AccessTask atask = new edu.cmu.mixer.access.AccessTask(numtasks);  
  o = atask.process(session, request);

  boolean complete = (o.optInt("taskno", 0) >= numtasks);
  o.putOpt("complete", complete);

  o.putOpt("eventname", "confirmtasks");
  o.putOpt("response", edu.cmu.mixer.access.EventLog.getInstance().log(o));    

  if (complete) {
    o.putOpt("eventname", "completedtasks");
    o.putOpt("response", edu.cmu.mixer.access.EventLog.getInstance().log(o));

    org.json.JSONObject orderings = o.optJSONObject("orderings");
    com.google.appengine.api.datastore.DatastoreService ds =
      com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();
    com.google.appengine.api.datastore.Entity taskstatus =
      ds.get(com.google.appengine.api.datastore.KeyFactory.createKey("OrderStatus", (Long) orderings.opt("taskstatusid")));
    taskstatus.setProperty("type", "completion");
    taskstatus.setProperty("comptime", edu.cmu.mixer.access.EventLog.getInstance().getTimestamp());
    ds.put(taskstatus);

    com.google.appengine.api.datastore.Entity condstatus =
      ds.get(com.google.appengine.api.datastore.KeyFactory.createKey("OrderStatus", (Long) orderings.opt("condstatusid")));
    condstatus.setProperty("type", "completion");
    condstatus.setProperty("comptime", edu.cmu.mixer.access.EventLog.getInstance().getTimestamp());
    ds.put(condstatus);
  }

  String conf = o.optString("hash");

  System.err.println("CTO: " + o.toString(2));

  if (request.getParameter("flush") != null) {
    session.invalidate();
  }
]]>
  </jsp:scriptlet>
  <c:set var="numtasks">
    <jsp:expression>numtasks</jsp:expression>
  </c:set>
  <c:set var="conf">
    <jsp:expression>conf</jsp:expression>
  </c:set>
  <c:set var="msg">
    <jsp:expression>complete ? "so much" : "a lot"</jsp:expression>
  </c:set>
  <jsp:text>
    <html>
      <head>
        <title> Thanks! </title>
        <link rel="stylesheet" type="text/css" href="access.css">
	  <!-- JSP workaround -->
	</link>
      </head>
      <body>
	<div class="main">
	  <div class="progress">
	    <span class="taskno">${numtasks}</span>
	    <span> of </span>
	    <span class="numtasks">${numtasks}</span>
	  </div>
          <div class="text">
	    <p id="thanks">Thanks ${msg}!</p>
            <form>
	      <div class="conf ccontainer">
		<label for="conf">
		  <span class="label">Your confirmation code is</span>
		</label>
		<p class="conf card" id="conf">${conf}</p>
	      </div>
	    </form>
	  </div>
	</div>
      </body>
    </html>
  </jsp:text>
</jsp:root>
