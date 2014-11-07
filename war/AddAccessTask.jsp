<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          version="1.2">
  <jsp:directive.page contentType="text/html;charset=UTF-8" language="java" />
  <jsp:scriptlet>
<![CDATA[
  org.json.JSONObject o = new org.json.JSONObject();
  o.putOpt("url", request.getParameter("url"));
  o.putOpt("taskno", request.getParameter("taskno"));
  o.putOpt("question", request.getParameter("question"));
  o.putOpt("title", request.getParameter("title"));

  o.putOpt("taskno", Integer.parseInt(o.optString("taskno", "-1")));

  if (o.has("url") && o.has("question")) {
    com.google.appengine.api.datastore.DatastoreService ds =
      com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();
    
     com.google.appengine.api.datastore.Entity entity =
       new com.google.appengine.api.datastore.Entity("AccessTask");

     entity.setProperty("representative", "");
     entity.setProperty("title", "");
     for (String key : org.json.JSONObject.getNames(o)) {
       entity.setProperty(key, o.opt(key));
     }
     ds.put(entity);
  }
]]>
  </jsp:scriptlet>
  <jsp:text>
    <html>
      <head>
        <title> Add Task </title>
        <style>
          .inviz { opacity: 0.5; }
        </style>
      </head>       
      <body>
        <form method="get">
          <center>
            <div>
              <input type="url" name="url" id="url"></input>
            </div>
            <div>
              <input type="text" name="title" id="title"></input>
            </div>
            <div>
              <input type="number" name="taskno" id="taskno" value="-1"></input>
            </div>
            <div>
              <textarea name="question" id="question">
                <!-- jsp parser workaround -->
              </textarea>
            </div>
            <div>
              <input type="submit" name="submit" id="submit" value="Submit"/>
            </div>
          </center>
        </form>
      </body>
    </html>
  </jsp:text>
</jsp:root>
