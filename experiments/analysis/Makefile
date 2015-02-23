
AccessTask.csv:
	curl "https://1412-dot-gae-wrapup-server.appspot.com/admin/bulk/AccessTask.csv?token=wrapmixerwrap&header=true" > $@

AccessEvent.csv:
	curl "https://1412-dot-gae-wrapup-server.appspot.com/admin/bulk/AccessEvent.csv?token=wrapmixerwrap&header=true&limit=10000" > $@

OrderStatus.csv:
	curl "https://1412-dot-gae-wrapup-server.appspot.com/admin/bulk/OrderStatus.csv?token=wrapmixerwrap&header=true" > $@

batch.csv: Batch_*results.csv
	ls $^ | head -1 | parallel head -n 1 {} > $@
	flip -u $@
	ls $^ | parallel tail -n +2 {} \; echo | perl -ne 'chomp; s/\r//g; print "$${_},\n";' >> $@

access.sqlite: idb.sql AccessEvent.csv AccessTask.csv OrderStatus.csv batch.csv
	rm -f $@
	(echo ".import AccessEvent.csv AccessEvent") | sqlite3 -csv $@
	(echo ".import AccessTask.csv AccessTask") | sqlite3 -csv $@
	(echo ".import OrderStatus.csv OrderStatus") | sqlite3 -csv $@
	(echo ".import batch.csv TurkResults") | sqlite3 -csv $@
	cat $< | sqlite3 $@

access2.sqlite: idb2.sql AccessEvent.csv AccessTask.csv OrderStatus.csv batch.csv
	rm -f $@
	(echo ".import AccessEvent.csv AccessEvent") | sqlite3 -csv $@
	(echo ".import AccessTask.csv AccessTask") | sqlite3 -csv $@
	(echo ".import OrderStatus.csv OrderStatus") | sqlite3 -csv $@
	(echo ".import batch.csv TurkResults") | sqlite3 -csv $@
	cat $< | sqlite3 $@

_pright.rin: access.sqlite
	echo "select taskid,pright,meanbad,meanright from taskrating ; " | sqlite3 -header $< > $@

_pright.rout: pright.r pright.rin
	cat $< | R --vanilla > $@

%.rin: access2.sqlite
	echo $@ | parallel echo "select \* from {.} \; " | sqlite3 -header $< > $@

%.rout: %.r %.rin
	cat $< | R --vanilla > $@

tasks.wiki: access.sqlite
	(echo ".sep '||'"; echo "select * from taskswiki ; ") | sqlite3 -header $< > $@
