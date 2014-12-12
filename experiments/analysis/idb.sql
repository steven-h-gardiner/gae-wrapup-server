
DROP VIEW IF EXISTS foo;

DROP VIEW IF EXISTS TaskTimes0;

CREATE VIEW IF NOT EXISTS TaskTimes0 AS
       SELECT *,
       	      (answer like '%no' or answer like '%now%UTC%') as defbad
       FROM AccessEvent
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
	      min("timestamp") AS  starttime,
	      max("timestamp") AS  stoptime
       FROM TaskTimes0
       GROUP BY hash,taskid
       ORDER BY "timestamp" ASC;
      	      

DROP VIEW IF EXISTS TaskTimes2;

CREATE VIEW IF NOT EXISTS TaskTimes2 AS
       SELECT *,
       	      (answers like '%no' or answers like '%now%UTC%') as defbad,
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

DROP VIEW IF EXISTS TaskTimes;

CREATE VIEW IF NOT EXISTS TaskTimes AS
       SELECT hash,
       	      taskid,
	      conditions as condition,
	      answers as answer,
       	      stopsecs - startsecs as delta,	      
	      A.url,
	      A.taskno
       FROM TaskTimes2 T LEFT OUTER JOIN AccessTask A ON T.taskid = A.id
       WHERE 1
       AND signature like '%submitanswer%'
       AND (taskid != '')
       AND 1;

DROP VIEW IF EXISTS AnswerKey;

CREATE VIEW IF NOT EXISTS AnswerKey AS
       SELECT taskid,
       	      answer
       FROM AccessEvent
       WHERE 1
       AND answercorrect IS NOT NULL
       AND 1;
