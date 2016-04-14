<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          version="1.2">
  <jsp:directive.page contentType="text/html;charset=UTF-8" language="java" />
  <jsp:scriptlet>
<![CDATA[

  if (request.getParameter("flush") != null) {
    edu.cmu.mixer.access.EventLog.getInstance().getTableFirst(request.getRemoteAddr(), 0);
    session.invalidate();
    response.sendRedirect("/AccessStudy.jsp" + ((request.getParameter("debug") == null) ? "" : "?debug=true"));
    return;
  }

  org.json.JSONObject event = new org.json.JSONObject();

  org.json.JSONObject guide = edu.cmu.mixer.access.AccessTask.getGuideObject(session, request);
  guide.putOpt("debug", request.getParameter("debug"));
  guide.putOpt("debug",  (guide.optString("debug", "").equals("")) ? ""     : "debug");
  guide.putOpt("method", (guide.optString("debug", "").equals("")) ? "post" : "get");

  event.putOpt("sessionid", guide.optString("sessionid"));
  event.putOpt("hash", guide.optString("sessionhash"));

  org.json.JSONObject answers = new org.json.JSONObject();
  try {
    answers = new org.json.JSONObject((String) session.getAttribute("answers"));
  } catch (Exception ex) {
    // ignore answers empty or in incorrect form
  }
  try {
    if (request.getParameter("answer") != null) {
      org.json.JSONObject answer = new org.json.JSONObject();
      answer.putOpt("answer", request.getParameter("answer"));
      answer.putOpt("taskid", request.getParameter("taskid"));
      answer.putOpt("ataskid", request.getParameter("ataskid"));
      answer.putOpt("condition", request.getParameter("condition"));
      
      if (answer.optString("answer", "").trim().equals("")) {
        guide.putOpt("validation", "nullanswer");
	throw new Exception("answer should not be empty!");
      }

      event.putOpt("taskno", answers.length());
      event.putOpt("taskid", answer.optString("ataskid"));
      event.putOpt("condition", answer.optString("condition"));
      event.putOpt("eventname", "submitanswer");
      event.putOpt("answer", answer.optString("answer"));

      guide.putOpt("answersubmission", 
                   edu.cmu.mixer.access.EventLog.getInstance().log(event));

      answers.put(answer.optString("taskid"), answer);
      session.setAttribute("answers", answers.toString());
    }
  } catch (Exception ex) {
    guide.putOpt("exception", ex.getMessage());
  }

  //guide.put("mode", guide.optString("mode", "practice"));
  //guide.put("tasknum", guide.optInt("tasknum", 300));

  //guide.put("mode", "ouch");

  guide.putOpt("ip", request.getRemoteAddr());

  org.json.JSONArray tasks = guide.optJSONArray("tasks");
  if (tasks == null) { tasks = new org.json.JSONArray(); }

  int tasknum = guide.optInt("tasknum", tasks.length());
  if (answers.length() >= tasknum) {
    event.putOpt("taskno", answers.length());
    event.putOpt("eventname", "confirmtasks");
    event.remove("answer");
  
    guide.putOpt("confirmtasks", 
                 edu.cmu.mixer.access.EventLog.getInstance().log(event));

    response.sendRedirect("/ConfirmTasks.jsp?numtasks=" + tasknum);
    return;
  }

  org.json.JSONObject task = tasks.optJSONObject(answers.length());
  if (task == null) { task = new org.json.JSONObject(); }

  //System.err.println("ANSWERS: " + session.getAttribute("answers"));
  
  guide.putOpt("answers", answers);
  guide.putOpt("task", task);
  
  boolean useTable = (answers.length() > guide.optInt("tasknum2", tasks.length() / 2));
  if (guide.optString("tablefirst", "false").equals("true")) { // invert for tablefirst
    useTable = ! useTable; 
  }
  if (task.optBoolean("practice", false)) {
    useTable = true;
    guide.putOpt("mode", "practice");
  } else {
    guide.putOpt("mode", useTable ? "tables" : "nontables");
  }
  guide.putOpt("condition", useTable ? 3 : 0);
  
  guide.putOpt("taskno1", 1+answers.length());

  String oldmode = (String) session.getAttribute("mode");
  if ((oldmode == null) || (! oldmode.equals(guide.optString("mode", "")))) {
    guide.putOpt("interstitial", "interstitial");
  }
  session.setAttribute("mode", guide.optString("mode", ""));

  event.putOpt("target", "tablefirst=" + guide.optString("tablefirst", "unset"));
  event.putOpt("taskno", answers.length());
  event.putOpt("taskid", task.optString("ataskid"));
  event.putOpt("condition", guide.optString("condition"));
  event.putOpt("eventname", "viewtask");
  event.remove("answer");
  
  guide.putOpt("questionview", 
               edu.cmu.mixer.access.EventLog.getInstance().log(event));

 
  //response.addCookie(new javax.servlet.http.Cookie("tablefirst", guide.optString("tablefirst", "false")));
  
]]>
  </jsp:scriptlet>
  <c:set var="taskno1">
    <jsp:expression>guide.optInt("taskno1")</jsp:expression>
  </c:set>
  <c:set var="numtasks">
    <jsp:expression>guide.optInt("numtasks")</jsp:expression>
  </c:set>
  <c:set var="mode">
    <jsp:expression>guide.optString("mode")</jsp:expression>
  </c:set>
  <c:set var="question">
    <jsp:expression>task.optString("question")</jsp:expression>
  </c:set>
  <c:set var="taskid">
    <jsp:expression>task.optInt("taskid")</jsp:expression>
  </c:set>
  <c:set var="ataskid">
    <jsp:expression>task.optString("ataskid")</jsp:expression>
  </c:set>
  <c:set var="condition">
   <jsp:expression>guide.optInt("condition", 0)</jsp:expression>
  </c:set>
  <c:set var="guideStr">
    <jsp:expression>guide.toString(2)</jsp:expression>
  </c:set>
  <c:set var="debug">
    <jsp:expression>guide.optString("debug", "")</jsp:expression>
  </c:set>
  <c:set var="hash">
    <jsp:expression>guide.optString("sessionhash", "")</jsp:expression>
  </c:set>
  <c:set var="validation">
    <jsp:expression>guide.optString("validation", "")</jsp:expression>
  </c:set>
  <c:set var="interstitial">
    <jsp:expression>guide.optString("interstitial", "")</jsp:expression>
  </c:set>
  <c:set var="method">
    <jsp:expression>guide.optString("method", "get")</jsp:expression>
  </c:set>
  <jsp:text>
    <html>
      <head>
        <title> View Task </title>
        <link rel="stylesheet" type="text/css" href="access.css">
	  <!-- JSP workaround -->
	</link>
	<style>

	  .main             .condition           { display: none;    }
	  .main.practice    .condition.practice  { display: inherit; }
	  .main.tables      .condition.tables    { display: inherit; }
	  .main.nontables   .condition.nontables { display: inherit; }

	  .main       div.debug { display: none;    }
	  .main.debug div.debug { display: inherit; }

	  body            .validation            { display: none; }
	  body.nullanswer .validation.nullanswer { display: inherit; }

	  body                          .interstitial           { display: none; }
	  body.interstitial .practice   .interstitial.practice  { display: inherit; }
	  body.interstitial .tables     .interstitial.tables    { display: inherit; }
	  body.interstitial .nontables  .interstitial.nontables { display: inherit; }
	  
	</style>
      </head>       
      <body class="${validation} ${interstitial}">
        <div class="main ${mode} ${debug}">
  	  <div class="validation ${validation}">
	    Please do not leave an answer blank.  If the answer is not
	    in the page please answer "no answer found."  If you wish
	    to skip a question without answering, please answer "pass."
	  </div>
  	  <div class="interstitial practice">
	    Let's begin with a practice task to get used to the format
	    of the tasks in the study.
	    <span class="payment">
	      Your performance on the practice task will not affect
	      your payment for the study.
	    </span>
	  </div>
	  <div class="interstitial tables">
	    The next several tasks present web pages that have been
	    enhanced to make use of tables.
	  </div>
	  <div class="interstitial nontables">
	    The next several tasks present web pages as they exist on
	    the web, without any enhancement.
	  </div>
	  <div class="progress">
	    <div>
	      <span class="taskno">${taskno1}</span>
	      <span> of </span>
	      <span class="numtasks">${numtasks}</span>
	    </div>
	    <div class="condition practice">
	      This is a practice task to let you get used to the format of the tasks.
	    </div>
	    <div class="condition tables">
	      The linked page has been rewritten to use tables.
	    </div>
	    <div class="condition nontables">
	      The linked page does not use tables.
	    </div>
	  </div>
          <div class="text">
	    <div class="instructions">
              <p>	 
	        Please enter the answer to the following question in
		the box below using the information from the linked
		page.  Every answer should be based on only the linked
		page, without visiting links or changing pages.
	      </p>
              <p>
		If the requested information is not present in
	        the linked page, just answer "No answer found" in the
	        box below.  If you would prefer to skip this page,
	        just answer "pass".
 	      </p>		
	    </div>
            <form method="${method}">
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
		  Please answer the preceding question by
		    <a class="mainlink" id="mainlink"
                       href="/atasks/${ataskid}${condition}.xhtml?hash=${hash}"
                       target="${accesstarget}">
		      consulting the
		      <span class="condition tables practice">table</span>
		      <span class="condition nontables">page</span>
		      at this link</a>.
		  Remember that you will not need to follow any links
		  from the page nor press any buttons.
		</div>   
              </div>
	      <div class="answer ccontainer">
		<label for="answer">
		  <span class="label">Answer</span>
		</label>
		<textarea id="answer" name="answer">
                  <!-- jsp parsing workaround -->
		</textarea>		
	      </div>
              <input type="hidden" id="taskid" name="taskid" value="${taskid}">
                <!-- jsp parsing workaround -->
              </input>
              <input type="hidden" id="ataskid" name="ataskid" value="${ataskid}">
                <!-- jsp parsing workaround -->
              </input>
              <input type="hidden" id="condition" name="condition" value="${condition}">
                <!-- jsp parsing workaround -->
              </input>
              <input type="hidden" id="debug" name="debug" value="${debug}">
                <!-- jsp parsing workaround -->
              </input>
	      <div class="button">
		<input type="submit" id="submit" name="submit" value="Submit">
                  <!-- jsp parsing workaround -->
		</input>
	      </div>
            </form>
	  </div>
	  <div class="debug">
	    <h3>DEBUG</h3>
	    <dl>
	      <dt>GUIDE</dt>
	      <dd>
	        <pre>
		  ${guideStr}
		</pre>
	      </dd>		
	    </dl>
	  </div>
 	</div>
      </body>
    </html>
  </jsp:text>
</jsp:root>
