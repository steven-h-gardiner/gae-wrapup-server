
Goal: Find the answer and timing data for Kristin's pilot run through
the tasks at the last meeting.

1. Observe that the pilot run occurred on 2017-02-09

2. Navigate to https://console.cloud.google.com/appengine (or if
you're a dinosaur, appspot.com)

3. Use the hamburger menu in the upper left corner, scroll down to
"Storage->Datastore"

4. Change the selected Kind to "AccessEvent"

5. Select the tab "Query by GQL"

6. Enter the query "SELECT * from AccessEvent order by timestamp DESC"
(no semicolon, case is important for table and field names)

7. Scroll down to events matching the date "2017-02-09" and note the
hash (in this case its 129F2J).

8. Project down to AccessEvent records with the right hash: "SELECT *
from AccessEvent WHERE hash = '129F2J'"

9. The answer is one record, the time is the difference between the
timestamp of the "viewtask" event and the timestamp of the
"submitanswer" event.

I find the datastore is ungainly for complex queries.  In practice, I
download the data and import it into a local sqlite database, then
write queries over that.