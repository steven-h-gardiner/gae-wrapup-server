DROP VIEW IF EXISTS StudyTasks;

CREATE VIEW IF NOT EXISTS StudyTasks AS
       SELECT *
       FROM Study
       WHERE 1
       AND 1*taskno >= 200
       AND (1*taskno <= 300 OR 1*taskno >= 1000)
       AND 1;


       
