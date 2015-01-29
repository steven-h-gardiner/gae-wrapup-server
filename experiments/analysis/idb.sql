
DROP VIEW IF EXISTS foo;

DROP VIEW IF EXISTS TaskTimes0;

CREATE VIEW IF NOT EXISTS TaskTimes0 AS
       SELECT *,
       	      (answer like '%no' or answer like '%now%UTC%') as defbad
       FROM AccessEvent
       WHERE 1
       AND taskno >= 0
       AND 1
       ORDER BY "timestamp" ASC;

DROP VIEW IF EXISTS TaskTimes1;

CREATE VIEW IF NOT EXISTS TaskTimes1 AS
       SELECT hash,
       	      taskid,
	      taskno,
       	      group_concat(eventname) as signature,
	      group_concat("timestamp"),
	      group_concat(distinct condition) as conditions,
	      group_concat(distinct answer) as answers,
	      (avg(answercorrect like 'true') >= 1.0) as answercorrect,
	      min("timestamp") AS  starttime,
	      max("timestamp") AS  stoptime
       FROM TaskTimes0
       GROUP BY hash,taskid
       ORDER BY "timestamp" ASC;
      	      

DROP VIEW IF EXISTS TaskTimes2;

CREATE VIEW IF NOT EXISTS TaskTimes2 AS
       SELECT *,
       	      (0
	       or answers like '%no%answer%'
	       or answers like '%no%found%'
	       or answers like '%now%UTC%'
	       or answers like '%error%'
	       or answers like '%bad%link%'
	       or answers like '%site%not%load%'
	       or answers like '%missing%'
	       or answers like '%no%link%'
	       or answers like '%nonesuch%'
	       or 0) as defbad,
       	      1.0 * (strftime('%s', substr(starttime, 1,19)) || '.' || substr(starttime, 21,3)) AS startsecs,
      	      1.0 * (strftime('%s', substr( stoptime, 1,19)) || '.' || substr( stoptime, 21,3)) AS stopsecs
       FROM TaskTimes1;
       	      
DROP VIEW IF EXISTS Tasks0;

CREATE VIEW IF NOT EXISTS Tasks0 AS
       SELECT taskid,
       	      taskno,
	      count(*) as trials,
       	      avg(defbad) as badness
       FROM TaskTimes2
       WHERE 1
       AND 1
       GROUP BY taskid
       ORDER BY badness DESC;

DROP VIEW IF EXISTS TaskTimes3;

CREATE VIEW IF NOT EXISTS TaskTimes3 AS
       SELECT hash,
       	      taskid,
	      conditions as condition,
	      answers,
	      stopsecs,
       	      stopsecs - startsecs as delta,
	      answercorrect,
	      defbad,
	      A.url,
	      A.question,
	      A.taskno
       FROM TaskTimes2 T LEFT OUTER JOIN AccessTask A ON T.taskid = A.id
       WHERE 1
       AND signature like '%submitanswer%'
       AND (taskid != '')
       AND 1;
       
DROP VIEW IF EXISTS Answers;

CREATE VIEW IF NOT EXISTS Answers AS
       SELECT *,
       	      id as eventid
       FROM AccessEvent
       WHERE 1
       AND answer IS NOT NULL
       AND answer != ''
       AND 1;

DROP VIEW IF EXISTS TaskTimes;

CREATE VIEW IF NOT EXISTS TaskTimes AS
       SELECT T.*,
       	      A.answer,
       	      A.eventid
       FROM TaskTimes3 T LEFT OUTER JOIN Answers A ON (T.hash = A.hash AND T.taskid = A.taskid)
       WHERE 1
       AND 1;

DROP VIEW IF EXISTS AnswerKey;

CREATE VIEW IF NOT EXISTS AnswerKey AS
       SELECT taskid,
       	      answer,
	      answercorrect,
	      eventid
       FROM Answers
       WHERE 1
       AND answercorrect IS NOT NULL
       AND answercorrect != ''
       AND 1;

DROP VIEW IF EXISTS GradedTasks;

CREATE VIEW IF NOT EXISTS GradedTasks AS
       SELECT E.hash,
	      E.taskid,
	      E.taskno,
       	      E.question,
       	      E.answer,
	      E.defbad,
	      E.stopsecs,
	      E.delta as elapsed,
	      E.condition as condition,
	      K.answercorrect,	      
	      CASE K.answercorrect WHEN 'true' THEN 1
	      	   		   WHEN 'false' then -1
				   ELSE 0 END AS grade2,
	      CASE K.answercorrect WHEN 'true' THEN 1
	      	   		   WHEN 'false' then 0 END AS grade,
	      E.eventid as eventid
       FROM TaskTimes E LEFT OUTER JOIN AnswerKey K ON (1 
            AND E.taskid = K.taskid
	    AND lower(E.answer) = lower(K.answer)
	    AND 1) 
       WHERE 1
       AND 1;

DROP VIEW IF EXISTS UngradedTasks0;

CREATE VIEW IF NOT EXISTS UngradedTasks0 AS
       SELECT *
       FROM GradedTasks
       WHERE 1
       AND grade = 0
       AND defbad = 0
       AND 1;

DROP VIEW IF EXISTS UngradedTasks;

CREATE VIEW IF NOT EXISTS UngradedTasks AS
       SELECT taskid,
       	      question,
       	      answer,
	      count(*) as card,
	      group_concat(eventid) as events
       FROM UngradedTasks0
       GROUP BY taskid,answer
       ORDER BY card ASC;
 	      
DROP VIEW IF EXISTS TaskBadness;

CREATE VIEW IF NOT EXISTS TaskBadness AS
       SELECT round(avg(G.defbad)*10000.0)/10000.0 AS meanbad,
	      G.taskid,
	      G.question
       FROM GradedTasks G
       WHERE 1
       AND taskno >= 0
       AND 1
       GROUP BY taskid
       ORDER BY meanbad ASC;
 	      
DROP VIEW IF EXISTS TaskRightness;

CREATE VIEW IF NOT EXISTS TaskRightness AS
       SELECT round(avg(G.grade)*10000.0)/10000.0 AS meanright,
	      G.taskid,
	      G.question
       FROM GradedTasks G
       WHERE 1
       AND taskno >= 0
       AND 1
       GROUP BY taskid
       ORDER BY meanright DESC;

DROP VIEW IF EXISTS AggTask;

CREATE VIEW IF NOT EXISTS AggTask AS
       SELECT G.taskid,
       	      T.url,
	      T.question,
	      T.taskno,
       	      avg(G.defbad) AS meanbad,
       	      avg(G.grade) AS meangrade,
	      avg(G.answercorrect IS NOT NULL) AS weight,
	      avg(abs(G.grade2)) * avg(G.answercorrect IS NOT NULL) AS gradedness,
	      avg(G.grade) * avg(G.answercorrect IS NOT NULL) AS weightedgrade
       FROM GradedTasks G LEFT OUTER JOIN AccessTask T ON G.taskid = T.id
       WHERE 1
       AND T.taskno >= 0
       AND 1
       GROUP BY taskid;

DROP VIEW IF EXISTS TaskRating;

CREATE VIEW IF NOT EXISTS TaskRating AS
       SELECT B.taskid,
       	      B.meanbad,
	      R.meanright,
	      (1-meanbad)*meanright as pright,
	      B.question
       FROM TaskBadness B LEFT OUTER JOIN TaskRightness R ON B.taskid = R.taskid
       WHERE 1
       AND 1
       ORDER BY pright ASC;

DROP VIEW IF EXISTS TaskQuintile;

CREATE VIEW IF NOT EXISTS TaskQuintile AS
       SELECT *,
       	      CASE WHEN pright >= 0.82 THEN 1
	      	   WHEN pright >= 0.615 THEN 0
	      	   WHEN pright >= 0.35 THEN -1
	      	   WHEN pright >= 0.09 THEN -2
	      	   ELSE -3 END AS sigma,
       	      CASE WHEN pright >= 0.82 THEN 5
	      	   WHEN pright >= 0.70 THEN 4
	      	   WHEN pright >= 0.65 THEN 3
	      	   WHEN pright >= 0.48 THEN 2
	      	   ELSE 0 END AS quintile
       FROM TaskRating
       WHERE 1
       AND 1
       ORDER BY pright ASC;

DROP VIEW IF EXISTS timeline;

CREATE VIEW IF NOT EXISTS timeline AS
       SELECT stopsecs as unixtime,
       	      grade2 as grade,
	      defbad as badness,
	      taskno
       FROM GradedTasks
       ORDER BY stopsecs ASC;


DROP VIEW IF EXISTS pright;

CREATE VIEW IF NOT EXISTS pright AS
       SELECT taskid,
       	      pright,
	      meanbad,
	      meanright
       FROM taskrating;

DROP VIEW IF EXISTS delta;

CREATE VIEW IF NOT EXISTS delta AS
       SELECT taskid,
       	      't'||taskno as taskno,
       	      condition,
       	      'c'||condition as condid,
       	      grade,
	      elapsed
       FROM gradedtasks
       WHERE 1
       AND condition like '_'
       AND taskid in (select taskid from accesstask where taskno >= 0)
       AND 1;

DROP VIEW IF EXISTS TasksWiki;

CREATE VIEW IF NOT EXISTS TasksWiki AS
       SELECT '' AS '',
              1+taskno as num,
              question,
              '[http://gae-wrapup-server.appspot.com/atasks/' || id || '0.xhtml raw]' as 'raw',
              '[http://gae-wrapup-server.appspot.com/atasks/' || id || '1.xhtml FU]' as 'fix underuse',
              '[http://gae-wrapup-server.appspot.com/atasks/' || id || '3.xhtml FO]' as 'fix overuse',
              '' AS ' '
       FROM AccessTask
       WHERE 1
       AND taskno >= 0
       AND 1
       ORDER BY 1*taskno asc;
