<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          version="1.2">
  <jsp:directive.page contentType="text/html;charset=UTF-8" language="java" />
  <jsp:scriptlet>
<![CDATA[

  org.json.JSONObject o = new org.json.JSONObject();
  o.putOpt("url", request.getParameter("url"));
  o.putOpt("taskno", request.getParameter("taskno"));
  o.putOpt("condition", request.getParameter("condition"));
  o.putOnce("condition", "0");

  o.putOpt("sessionid", session.getId());
  //o.putOpt("md5sum", edu.cmu.mixer.util.ServletUtil.getMD5Sum(o.optString("sessionid", o.toString())));
  o.putOpt("hash",Integer.toString(Math.abs(o.optString("sessionid", o.toString()).hashCode()), 32).toUpperCase());
  //o.putOpt("uuid", java.util.UUID.nameUUIDFromBytes(o.optString("sessionid", o.toString()).getBytes()).toString());
  o.putOpt("sessioninterval", session.getMaxInactiveInterval());

  o.putOpt("taskno", Integer.parseInt(o.optString("taskno", "-1")));

  if (request.getParameter("flush") != null) {
    session.invalidate();
  }

  o.putOpt("eventname", "viewtask");
  o.putOpt("response", edu.cmu.mixer.access.EventLog.getInstance().log(o));

  o.putOpt("orderings", edu.cmu.mixer.access.EventLog.getInstance().drawOrderings(o));

  com.google.appengine.api.datastore.Entity entity = 
    new com.google.appengine.api.datastore.Entity("AccessTask");
  // default so non-null but never stored to datastore

  com.google.appengine.api.datastore.DatastoreService ds =
    com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();
  com.google.appengine.api.datastore.Query query = 
    new com.google.appengine.api.datastore.Query("AccessTask");
  com.google.appengine.api.datastore.PreparedQuery pq = null;
  if (o.has("url")) {
    query = query.setFilter(new com.google.appengine.api.datastore.Query.FilterPredicate("url",
                                                                                         com.google.appengine.api.datastore.Query.FilterOperator.EQUAL,
                                                                                         o.optString("url")));

    pq = ds.prepare(query);
    for (com.google.appengine.api.datastore.Entity result : pq.asIterable()) {
      entity = result;
    }
  }
  if (o.optInt("taskno", -1) >= 0) {

    query = query.setFilter(new com.google.appengine.api.datastore.Query.FilterPredicate("taskno",
                                                                                         com.google.appengine.api.datastore.Query.FilterOperator.EQUAL,
                                                                                         o.optInt("taskno", -1)));

    pq = ds.prepare(query);
  }
  if (pq != null) {
    for (com.google.appengine.api.datastore.Entity result : pq.asIterable()) {
      entity = result;
    }
  }

  o.putOpt("title", entity.getProperty("title"));
]]>
  </jsp:scriptlet>
  <c:set var="response">
   <jsp:expression>o.optJSONObject("response").toString()</jsp:expression>
  </c:set>
  <c:set var="interval">
   <jsp:expression>o.optString("sessioninterval")</jsp:expression>
  </c:set>
  <c:set var="url">
   <jsp:expression>entity.getProperty("url")</jsp:expression>
  </c:set>
  <c:set var="sessionid">
   <jsp:expression>o.optString("sessionid")</jsp:expression>
  </c:set>
  <!--
  <c:set var="md5sum">
   <jsp:expression>o.optString("md5sum")</jsp:expression>
  </c:set>
  <c:set var="uuid">
   <jsp:expression>o.optString("uuid")</jsp:expression>
  </c:set>
  -->
  <c:set var="hash">
   <jsp:expression>o.optString("hash")</jsp:expression>
  </c:set>
  <c:set var="question">
   <jsp:expression>entity.getProperty("question")</jsp:expression>
  </c:set>
  <c:set var="id">
   <jsp:expression>entity.getKey().getId()</jsp:expression>
  </c:set>
  <c:set var="condition">
   <jsp:expression>o.optString("condition", "0")</jsp:expression>
  </c:set>
  <c:set var="title">
   <jsp:expression>o.optString("title", "Unnamed Page")</jsp:expression>
  </c:set>
  <jsp:text>
    <html>
      <head>
        <title> View Task </title>
        <style>
          .inviz { opacity: 0.5; }
          .label { width: 20%; padding-right: 15%; }
          div.main { margin-left: auto; margin-right: auto; width: 40%; } 
        </style>
      </head>       
      <body>
        <div class="main">
          <div>
            <span class="label">SESSIONID</span>
            <span>${sessionid}</span>
            
          </div>
<!--
	  <div>
            <span class="label">MD5SUM</span>
            <span>${md5sum}</span>
            
          </div>
          <div>
            <span class="label">UUID</span>
            <span>${uuid}</span>
            
          </div>
-->	
          <div>
            <span class="label">HASH</span>
            <span>|| ${hash} ||</span>
            
          </div>
          <div>
            <span class="label">SESSIONINTERVAL</span>
            <span>${interval}</span>
            
          </div>
          <div>
            <span class="label">RESPONSE</span>
            <span>${response}</span>
            
          </div>
          <div>
            <span class="label">TASKID</span>
            <span>${id}</span>
            
          </div>
         <div>
            <span class="label">URL</span>
            <span>${url}</span>
            
          </div>
          <div>
            <span class="label">QUESTION</span>
            <span>${question}</span>
            
          </div>
          <div>
            <span class="label">CONDITION</span>
            <span>${condition}</span>
            
          </div>
          <div class="text">
            <p>
              Please find the answer to the following question in the
              information presented in the linked page, and enter the
              answer in the box below.
            </p>
            <p>
              <span class="question">${question}</span>
            </p>
            <p>
              <a class="mainlink" href="/atask/${id}${condition}.html">${title}</a>
            </p>
            <form method="get">
              <textarea id="answer" name="answer">
                <!-- jsp parsing workaround -->
              </textarea>
              <input type="submit" id="submit" name="submit" value="Submit">
                <!-- jsp parsing workaround -->
              </input>
            </form>
          </div>
        </div>
      </body> 
    </html>
 </jsp:text>
</jsp:root>
