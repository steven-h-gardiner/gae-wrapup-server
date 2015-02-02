DROP VIEW IF EXISTS Answers;
CREATE VIEW IF NOT EXISTS Answers AS
       SELECT E.hash,
              E.answer,
              E.answercorrect,
              E.taskid,
              E.timestamp,
              T.taskno
       FROM AccessEvent E LEFT OUTER JOIN AccessTask T ON (E.taskid = T.id)
       WHERE 1
       AND eventname = 'submitanswer'
       AND T.taskno >= 0
       AND 1;
       
DROP VIEW IF EXISTS AnswerKey0;
CREATE VIEW IF NOT EXISTS AnswerKey0 AS
       SELECT A.answer,
              A.answercorrect,
              CASE A.answercorrect WHEN 'true' THEN 1
                                   WHEN 'false' THEN 0
              ELSE 0.1
              END AS grade,
              A.taskid,
              T.taskno
       FROM Answers A LEFT OUTER JOIN AccessTask T ON (A.taskid = T.id)
       WHERE 1
       AND answercorrect != ''
       AND 1;

DROP VIEW IF EXISTS RightAnswers;
CREATE VIEW IF NOT EXISTS RightAnswers AS
     SELECT taskid,
            taskno,
            answer,
            answercorrect
     FROM AnswerKey0
     WHERE 1
     AND answercorrect = 'true'
     AND 1;

DROP VIEW IF EXISTS WrongAnswers;
CREATE VIEW IF NOT EXISTS WrongAnswers AS
     SELECT taskid,
            taskno,
            answer,
            answercorrect
     FROM AnswerKey0
     WHERE 1
     AND answercorrect != 'true'
     AND 1;

DROP VIEW IF EXISTS AnswerKey1;

CREATE VIEW IF NOT EXISTS AnswerKey1 AS
       SELECT A.hash,
              A.answer,
              A.taskid,
              A.taskno,
              A.timestamp,
              T.question,
              K.answercorrect,
              K.grade as rawgrade
       FROM Answers A LEFT OUTER JOIN AnswerKey0 K ON (A.taskid = K.taskid AND lower(trim(A.answer)) = lower(trim(K.answer))) JOIN AccessTask T ON (A.taskid = T.id)
       WHERE 1
       AND 1;

DROP VIEW IF EXISTS AnswerKey;

CREATE VIEW IF NOT EXISTS AnswerKey AS
       SELECT *,
              (rawgrade IS NOT NULL) as graded,
              CASE WHEN rawgrade IS NULL THEN 0.1
              ELSE rawgrade END AS grade
       FROM AnswerKey1
       WHERE 1
       AND 1;

DROP VIEW IF EXISTS Turk0;

CREATE VIEW IF NOT EXISTS Turk0 AS
       SELECT WorkerId,
              hitid,
	      substr(RequesterAnnotation,9) as batchno,
              SubmitTime,
              "Answer.surveycode" as hash
       FROM TurkResults
       WHERE 1       
       AND 1;

DROP VIEW IF EXISTS Event1;

CREATE VIEW IF NOT EXISTS Event1 AS
       SELECT hash,
              answer,
              answercorrect,
              taskid,
              taskno
       FROM AccessEvent
       WHERE 1
       AND eventname = 'submitanswer'
       AND 1;

DROP VIEW IF EXISTS TurkAnswer;

CREATE VIEW IF NOT EXISTS TurkAnswer AS
       SELECT MT.*,
              E.*
       FROM Turk0 MT JOIN AnswerKey E ON (MT.hash = E.hash)
       WHERE 1       
       AND 1;

DROP VIEW IF EXISTS RecentAnswer;

CREATE VIEW IF NOT EXISTS RecentAnswer AS
       SELECT *
       FROM AnswerKey 
       WHERE 1
       AND timestamp > '2015-01-25'
       AND 1;

DROP VIEW IF EXISTS TurkAnswer1;

CREATE VIEW IF NOT EXISTS TurkAnswer1 AS
       SELECT *,
       	      CASE WHEN graded THEN NULL
	      	   ELSE '|' || answer || '|' END as unanswer
       FROM TurkAnswer;

DROP VIEW IF EXISTS TurkSummary;

CREATE VIEW IF NOT EXISTS TurkSummary0 AS
       SELECT batchno,
       	      taskno,
       	      question,
	      avg(grade) as meangrade,
	      avg(graded) as meangraded,
	      group_concat(distinct(graded)) as graded,
	      group_concat(distinct(answer)) as answers,
	      group_concat(distinct(unanswer)) as unanswers
       FROM TurkAnswer1
       WHERE 1
       AND 1
       GROUP BY batchno,question;

DROP VIEW IF EXISTS TurkSummary;

CREATE VIEW IF NOT EXISTS TurkSummary AS
       SELECT batchno,question,meangrade,meangraded FROM TurkSummary0;

DROP VIEW IF EXISTS TurkSummary1;

CREATE VIEW IF NOT EXISTS TurkSummary1 AS
       SELECT *
       FROM TurkSummary0
       WHERE 1
       AND batchno in (select distinct batchno from turk0 order by 1*batchno desc limit 1)
       AND 1;
