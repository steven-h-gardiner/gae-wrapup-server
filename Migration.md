# Exporting Response records from sw-auth app #

The sw-auth google app engine instance has a mechanism for exporting records, but it is not terribly convenient.

There is a
[shell script](https://code.google.com/p/gae-wrapup-server/source/browse/doc/migration/bulk_export.sh) using the mechanism for Response records.  When run it downloads Response records as JSON arrays, in blocks of 100.

The export mechanism uses cached records, so you have to call it twice.  The first call enqueues a task to generate the files; sometime later the files are generated.  Sometime after that (five-ten minutes should be plenty), you should call the mechanism again, and you'll download the files from the cache.  You can check the "Task Queues" link of the sw-auth app to see whether there are zero tasks queued up, to tell whether the exports have completed.

The mechanism caches by the precise query string used, so it should not be changed between calls.

The blocksize can be altered, but the export mechanism tends to crash if the blocksize is too large.