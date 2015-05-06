<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          version="1.2">
  <jsp:directive.page contentType="text/html;charset=UTF-8" language="java" />
  <jsp:scriptlet>
<![CDATA[

  edu.cmu.mixer.access.AccessTask atask = new edu.cmu.mixer.access.AccessTask(8);  
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
   <jsp:expression>o.optInt("taskno", 0)</jsp:expression>
  </c:set>
  <c:set var="taskno1">
   <jsp:expression>1+o.optInt("taskno", 0)</jsp:expression>
  </c:set>
  <c:set var="numtasks">
   <jsp:expression>atask.numtasks</jsp:expression>
  </c:set>
  <c:set var="pctdone">
   <jsp:expression>Math.round((0.0+o.optInt("taskno", 0)) / (0.01*atask.numtasks))</jsp:expression>
  </c:set>
  <c:set var="donebr">
   <jsp:expression>Math.min(2+Math.round((0.0+o.optInt("taskno", 0)) / (0.01*atask.numtasks)), 100)</jsp:expression>
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
  <c:set var="accesstarget">
   <jsp:expression>o.optString("accesstarget", "")</jsp:expression>
  </c:set>
  <jsp:text>
    <html>
      <head>
        <title> View Task </title>
        <link rel="stylesheet" type="text/css" href="access.css">
	  <!-- JSP workaround -->
	</link>
	<style>
          .instructions {
            display: none;
          }
	  .progress {
	    background: linear-gradient(to right, rgba(210,162,45,1.0), rgba(210,162,45,0.9) ${pctdone}%, rgba(210,162,45,0.6) ${donebr}%, rgba(210,162,45,0.3));
	  }
        </style>
      </head>       
      <body>
        <div class="main">
<!--
          <div>
            <span class="label">SESSIONID</span>
            <span>${sessionid}</span>
            
          </div>
	  <div>
            <span class="label">MD5SUM</span>
            <span>${md5sum}</span>
            
          </div>
          <div>
            <span class="label">UUID</span>
            <span>${uuid}</span>
            
          </div>
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
-->	
	  <div class="progress">
	    <span class="taskno">${taskno1}</span>
	    <span> of </span>
	    <span class="numtasks">${numtasks}</span>
	  </div>
          <div class="text">
	    <div class="instructions">
              <p>	 
		Please find the answer to the following question in
		the information presented in the linked page, and
		enter the answer in the box below.
              </p>
              <p>
	        Every question should be answered based on only the linked page,
	        without visiting any links or changing pages.  If the
	        requested information is not present in the linked
	        page, just answer "No answer found" in the box below.
	        If you would prefer to skip this page, just answer
	        "pass".
 	      </p>		
	    </div>
            <form method="get">
              <div class="question ccontainer">
		<label for="question">
		  <span class="label">Question</span>
		</label>
		<p class="question card" id="question">${question}</p>
              </div>
              <div class="link ccontainer">
              <!--
		<label for="mainlink">
		  <span class="label">Link</span>
		</label>
                -->
     		<div class="mainlink card">
		  Please answer the preceding question by <a
                  class="mainlink" id="mainlink"
                  href="/atasks/${id}${condition}.xhtml?hash=${hash}"
                  target="${accesstarget}">consulting the page at this
                  link</a>.  Remember that you will not need to follow
                  any links from the page nor press any buttons.
		</div>   
              </div>
              <input type="hidden" id="taskid" name="taskid" value="${id}">
                <!-- jsp parsing workaround -->
              </input>
              <input type="hidden" id="taskno" name="taskno" value="${taskno}">
                <!-- jsp parsing workaround -->
              </input>
              <input type="hidden" id="condition" name="condition" value="${condition}">
                <!-- jsp parsing workaround -->
              </input>
	      <div class="answer ccontainer">
		<label for="answer">
		  <span class="label">Answer</span>
		</label>
		<textarea id="answer" name="answer">
                  <!-- jsp parsing workaround -->
		</textarea>		
	      </div>
	      <div class="button">
		<input type="submit" id="submit" name="submit" value="Submit">
                  <!-- jsp parsing workaround -->
		</input>
	      </div>
            </form>
          </div>
        </div>
      </body> 
    </html>
 </jsp:text>
</jsp:root>
