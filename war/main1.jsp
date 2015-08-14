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

]]>
  </jsp:scriptlet>
  <c:set var="hash">
   <jsp:expression>o.optString("hash")</jsp:expression>
  </c:set>
  <jsp:text>
    <html>
      <head>
        <title>Entable study main page</title>
        <style>
          body {
          margin: 10%;
          }

        .offscreen {
          position:absolute;
          left:-10000px;
          top:auto;
          width:1px;
          height:1px;
          overflow:hidden;
        }

        </style>
        <script src="./js/jquery-2.1.3.min.js">
          // work-around for JSP parsing
        </script>
        <script>
          jQuery(document).on('ready', function() {
            jQuery('#surveyFrame').on("load", function() {
              var href = jQuery('#surveyFrame').get(0).contentDocument.location.href;
              if (href.match(/\?/)) {
                jQuery('#surveyFrame, #surveyThanks').toggle();
              }
            });
            jQuery('#consentFrame').on('load', function() {
              var href = jQuery('#consentFrame').get(0).contentDocument.location.href;
              if (href.match(/\?/)) {
                jQuery('#consentFrame, #consentThanks').toggle();
              } else {
                //no-op
              }
            });
          });
        </script>
      </head>
      <body>
        <h2>Welcome!</h2>
        <div>
          Thanks for participating in this study!
        </div>
	<h2 id="meta">About the study</h2>
	<p>
	  The purpose of the study is to determine whether websites
	  presenting data as tables are easier to use with a screen
	  reader than websites presenting the same information as a
	  formatted list.
	</p>
	<p>
	  The study is being conducted by Steven Gardiner, Anthony
	  Tomasic and John Zimmerman, researchers at Carnegie Mellon
	  University.  
        </p>
	<p>
	  The study takes place online and will take between 20 and 40
	  minutes.
	</p>	  
        <h2 id="procedure">Procedure</h2>
        <p>
          This study has three steps.
        </p>
        <ol>
          <li>First, you should review the <a
            href="onlineConsent.html">consent form here</a> to
            understand your rights and what data will be collected
            from your participation in the study.</li>
          <li>Next you'll be asked to complete a brief survey about your
	    experience with screen readers.</li>
          <li>Finally, you'll be asked to complete a number of lookup tasks
	    in webpages.</li>
            <!--
          <li>We'll finish by chatting briefly about what happened
	    during the tasks.</li>
            -->
        </ol>    
        <p>
          The tasks may be difficult and/or annoying.  We ask you to
          continue to struggle for awhile with them, but do remember
          that you are free to stop working on any task or the entire
          study at any time.
          <!--
              The other parts of the study should not
          be annoying or frustrating, please let me know if something
          is not working well!
            -->
        </p>
        <ol>
          <li>
            <h2 id="consent">Consent</h2>
            <iframe id="consentFrame" width="80%" height="270px" src="affirm.html#hash=${hash}">
              <!-- workaround for JSP parsing -->
            </iframe>
            <div id="consentThanks" style="display: none; width: 40%; height: 170px; border: 1px solid black; padding: 50px 20%;">
	      <p>Your responses have been saved.</p>
	      <p>Please continue the study below.</p>
	    </div>
          </li>
          <li>
            <h2 id="survey">Survey</h2>
            <!--
            <p>
              To help us match surveys to participants, please enter
              the following participation code in the survey: <span
              class="hash">${hash}</span>.
            </p>
            -->
            <iframe title="Survey" id="surveyFrame" src="survey.html#hash=${hash}" _src="https://www.surveymonkey.com/r/WZBRYV3" width="80%" height="400px">
	      <!-- workaround for JSP parsing -->
	    </iframe>
	    <div id="surveyThanks" style="display: none; width: 40%; height: 300px; border: 1px solid black; padding: 50px 20%;">
	      <p>Your survey responses have been saved.</p>
	      <p>Please continue the study below.</p>
	    </div>
            
          </li>
          <li>
            <h2 id="tasks">Tasks</h2>
            <p>
              We are evaluating how the structure of webpages affects
              your ability to lookup information on that page.  Each
              of the following tasks will present you with a factual
              question and a link to a page you'll consult for the
              answer to the question.  We are asking you to find the
              best answer on that page.  You will not need to follow
              any links from the page, or press any buttons on the
              page.
            </p>
            <p>
              You should work at a pace comfortable for you.  Since we
              do measure the time taken on tasks, please work on the
              tasks continuously and minimize other distractions.  Now
              is a good time to finish other stuff before beginning
              the first task, if you need to.
            </p>
            <p>
              Some pages may not contain an answer to the question.
              If you feel you've understood the page's contents and
              that the answer is not there, you can answer "no answer
              found".  Additionally, some pages may be so difficult to
              access that you might want to abandon the task after
              trying for some time.  Simply answer "pass" to those
              questions.
            </p>
	    <p>
              Now, please follow <a href="ViewAccessTask.jsp">this link to
                begin the tasks</a>
            </p>
          </li>
        </ol>
        <h2 id="thanks">Thanks!</h2>
        <p>That's all!  Thanks for helping us with the study!</p>
      </body>
    </html>
  </jsp:text>
</jsp:root>
