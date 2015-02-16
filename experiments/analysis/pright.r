
pright <- read.table('pright.rin', sep='|',header=T);

length(pright$pright);
log(pright$pright);

mean(pright$pright,na.rm=TRUE) + c(-2,-1,0,1,2) * sd(pright$pright,na.rm=TRUE);
exp(mean(log(pright$pright+0.001),na.rm=TRUE) + c(-2,-1,0,1,2) * sd(log(pright$pright+0.001),na.rm=TRUE));

quantile(pright$pright, probs=0:5/5, na.rm=TRUE);

plot(pright$pright);