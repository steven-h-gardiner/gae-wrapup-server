<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          version="1.2">
  <jsp:directive.page contentType="text/html;charset=UTF-8" language="java" />
  <jsp:scriptlet>
<![CDATA[

  edu.cmu.mixer.access.AccessTask atask = new edu.cmu.mixer.access.AccessTask(3);  
  org.json.JSONObject o = atask.process(session, request);

  if (request.getParameter("flush") != null) {
    session.invalidate();
  }

  if (o.has("finished")) {
    response.sendRedirect("/ConfirmTasks.jsp?numtasks=" + atask.numtasks);
    return;
  }

]]>
  </jsp:scriptlet>
  <c:set var="response">
   <jsp:expression>o.optJSONObject("response").toString()</jsp:expression>
  </c:set>
  <c:set var="interval">
   <jsp:expression>o.optString("sessioninterval")</jsp:expression>
  </c:set>
  <c:set var="url">
   <jsp:expression>o.optString("url")</jsp:expression>
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
   <jsp:expression>o.optString("question")</jsp:expression>
  </c:set>
  <c:set var="taskno">
   <jsp:expression>1+o.optInt("taskno", 0)</jsp:expression>
  </c:set>
  <c:set var="numtasks">
   <jsp:expression>atask.numtasks</jsp:expression>
  </c:set>
  <c:set var="id">
   <jsp:expression>o.optString("taskid", "TASK" + o.optString("pageno"))</jsp:expression>
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
	  <div class="progress">
	    <span class="taskno">${taskno}</span>
	    <span> of </span>
	    <span class="numtasks">${numtasks}</span>
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
              <input type="hidden" id="taskid" name="taskid" value="${id}">
                <!-- jsp parsing workaround -->
              </input>
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
