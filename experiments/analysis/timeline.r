
timeline <- read.table('timeline.rin', sep='|', header=TRUE);

#timeline;
timeline$whenstamp = as.POSIXct(timeline$unixtime, origin="1970-01-01");

plot(timeline$whenstamp, timeline$grade);
plot(timeline$whenstamp, timeline$badness);

plot(timeline$whenstamp, timeline$taskno);

