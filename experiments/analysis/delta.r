library(lme4);

delta <- read.table('delta.rin', sep='|', header=TRUE);

delta$taskname <- paste('task', delta$taskid);

plot(delta$condition, delta$elapsed);

t.test(delta$elapsed[delta$condition==0],delta$elapsed[delta$condition==1]);
t.test(delta$elapsed[delta$condition==0],delta$elapsed[delta$condition==3]);
t.test(delta$elapsed[delta$condition==3],delta$elapsed[delta$condition==1]);

t.test(delta$grade[delta$condition==0],delta$grade[delta$condition==1]);
t.test(delta$grade[delta$condition==0],delta$grade[delta$condition==3]);
t.test(delta$grade[delta$condition==3],delta$grade[delta$condition==1]);

plot(delta$taskno, delta$elapsed);

fit1 <- glm(grade~I(condition==0), data=delta, family=binomial);
fit1;
summary(fit1);

I(delta$condition == 0);

fit2 <- glm(elapsed~I(condition==3), data=delta);
fit2;
summary(fit2);

summary(glm(grade~I(condition==3), data=delta, family=binomial));

summary(lmer(elapsed~I(condition==0)+(1|taskno), data=delta));

summary(lmer(elapsed~condid+(1|taskno), data=delta));

