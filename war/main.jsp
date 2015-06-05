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
            jQuery(document).on('logconsent', function() {
              var data = jQuery(document).data('consent') || {};
              data.hash = jQuery(".hash").first().text();
              data.eventname = 'consent';
              jQuery(".consentBox").each(function(ix,elt) { data[elt.id] = elt.checked; });
              jQuery(document).data('consent', data);

              jQuery.ajax({url:'/access/log',data:data});                               

              console.log("LOGME: " + JSON.stringify(data));
            });
            jQuery("#consented").on('change', function() {
              jQuery(document).trigger("logconsent");
            });
            jQuery(".modality").on('change', function() {
              if (this.checked) { 
                jQuery(".modality").each(function(ix,elt) { elt.checked = false; });
                this.checked = true;
              }

              jQuery(document).trigger("logconsent");
            });
          });
        </script>
      </head>
      <body>
        <h2>Welcome!</h2>
        <div>
          Thanks for participating in this study!
        </div>
        <h2 id="meta">Instructions</h2>
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
            <p>
              Please check the boxes below to indicate that you've
              read and understood the consent information presented in
              the <a href="onlineConsent.html">consent form</a>.  Any
              questions about any part of the consent form or process
              can be addressed to the investigators, whose contact
              information is listed on the form.
            </p>                      
            <p>
              <label><input type="checkbox" name="consented" class="consentBox"
              id="consented" _checked="false"></input>I have read and
              agree to the consent form</label>
            </p>
            <p class="offscreen">
              <label><input type="checkbox" name="screenreader" class="modality consentBox"
              id="screenreader" _checked="false"></input>I am using a
              screen reader to complete the study</label>
            </p>          
            <p aria-hidden="true">
              <label><input type="checkbox" name="viz" class="modality consentBox"
              id="viz" _checked="false"></input>I am using a
              visual browser to complete the study</label>
            </p>          
          </li>
          <li>
            <h2 id="survey">Survey</h2>
            <p>
              To help us match surveys to participants, please enter
              the following identifier in the survey: <span
              class="hash">${hash}</span>.  Please follow <a
              href="redirect.jsp?url=https://www.surveymonkey.com/s/VDD2VFS&amp;hash=${hash}">this
              link to complete the survey</a>, then return here and
              continue the study.
            </p>
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
