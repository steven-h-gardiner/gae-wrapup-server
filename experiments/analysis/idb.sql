
DROP VIEW IF EXISTS foo;

DROP VIEW IF EXISTS TaskTimes0;

CREATE VIEW IF NOT EXISTS TaskTimes0 AS
       SELECT *
       FROM AccessEvent
       ORDER BY "timestamp" ASC;
       	      

DROP VIEW IF EXISTS TaskTimes1;

CREATE VIEW IF NOT EXISTS TaskTimes1 AS
       SELECT hash,
       	      taskid,
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
       	      1.0 * (strftime('%s', substr(starttime, 1,19)) || '.' || substr(starttime, 21,3)) AS startsecs,
      	      1.0 * (strftime('%s', substr( stoptime, 1,19)) || '.' || substr( stoptime, 21,3)) AS stopsecs
       FROM TaskTimes1;

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
