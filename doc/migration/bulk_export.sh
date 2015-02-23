#for n in `jot -w "%02d" 7 0`; do # mac
for n in `seq -w 0 14`; do # UNIX
    (find . -name Response0${n}00.json -size +500 -printf "%f EXISTS" | grep EXISTS) || echo Response0${n}00.json
    (find . -name Response0${n}00.json -size +500 -printf "%f EXISTS" | grep EXISTS) || curl -s "https://sw-auth.appspot.com/admin/bulk/Response.json?format=json&header=true&defer=true&useCache=true&token=abcd1253&limit=100&offset=0${n}00" > Response0${n}00.json
done
