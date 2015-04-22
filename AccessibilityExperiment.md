# Introduction #

The `<table/>` tag was standardized in HTML 3, combining existing
formalisms for representing tabular structures on web pages.  The
semantic mandate for the `<table/>` tag was to represent
information that book authors would put in tables, i.e. where an
author decides that a two-dimensional structure will be helpful to
understanding the contents.  Since its introduction, the
`<table/>` tag has been used for this semantic purpose, but just
as prevalently has been used for its presentational property that it
aligns its contents in a grid.  The prevalence of both uses of the
`<table/>` tag causes two usability problems for screenreader
uses.  Specifically, the `<table/>` tag is _overused_, and it is
_underused_.

The _`<table/>` tag overuse problem_ occurs when the
`<table/>` tag is used strictly for its presentational effect, to
align its contents in a grid layout.  This overuse was ubiquitous on
the web of the nineties, when no other mechanism was available to
achieve the grid effect.  Since that time, the overuse has abated
somewhat, as additional mechanisms (e.g. CSS2) have been implemented
and publicized.  However,
[Cafarella et al](https://gae-wrapup-server.googlecode.com/hg/doc/bib/2008_cafarellaWebTables.pdf)
show that the vast majority of instances of the `<table/>` tag
still (as of 2008) do not correspond with relations, and thus that the
`<table/>` tag is still overused.

Overuse of the `<table/>` tag probably causes some difficulty for
screenreader users.  Screenreaders provide users with two modes for
reading tables.  In one mode the screenreader reads the contents of
the page in (mostly) document order.  In a second _table mode_, the
screenreader provides commands for moving within the two-dimensional
structure of the table, i.e. along a row or a column.  The _table
mode_ also provides a command for consulting the header associated with
the row/column without affecting the cursor position.  For each
`<table/>` tag the screenreader software and/or the screenreader
user must make a decision about whether to use the _table mode_ or
not.  This decision itself consumes cognitive effort, in addition to
the effort of recovering from a previous wrong decision.

The _`<table/>` tag underuse problem_ occurs when the content
actually has a two-dimensional structure but the information structure
is portrayed visually without the semantics of the `<table/>` tag.
In the SmartWrap work, we analyzed many web datasets, which have a
natural two-dimensional layout wherein each row contains a tuple and
each column presents a particular attribute of each tuple.  We found
that many datasets were presented using a list layout or a grid
layout.  These layouts may make use of the `<table/>` tag but do
not use its "natural" semantics.

Underuse of the `<table/>` tag makes it more difficult for
screenreader users to understand a dataset.  The _table mode_ simply
cannot be used on datasets which are not marked up using the
`<table/>` tag.  The _table mode_ presents a substantial usability
enhancement for screenreader users, and has accordingly been adopted
by nearly all commercial screenreaders.  Without recourse to the
_table mode_, dataset lookups are
[prohibitively difficult](https://gae-wrapup-server.googlecode.com/hg/doc/bib/1997_gundersonUsabilityWWWVizImpaired.pdf).
List and grid layouts are used to appear aesthetically pleasing for
sighted users, but degrade the experience for screenreader users.

An additional axis of analysis is users' familiarity with the pages of
interest.  For websites the user has visited recently or uses
frequently, it is likely the user has
[adapted a strategy](https://gae-wrapup-server.googlecode.com/hg/doc/bib/2010_borodinMoreThanMeetsTheEye.pdf)
for more quickly locating the information of interest.  For example,
the user may remember which `<table/` tags do not present datasets
and may be safely "skipped," or may have memorized the structure of
datasets that do not use the `<table/` tag, allowing quick access
into them (e.g. look for the text after the "spacer.gif" image).  We
would expect the problems caused by `<table/>` tag overuse and
underuse to present more strongly in pages the user has not
encountered before; for pages the user has consulted before, the
adaptive strategies adopted may mitigate or neutralize the problems.

The purpose of this study is to establish that the usability problems
for screenreader users caused by table misuse are significant enough
to warrant a large-scale effort to mitigate the misuse.

# Summary of argument #

  1. Websites routinely underuse `<table/>` tags, presenting the semantics of datasets only visually as opposed to the dataset semantics of the `<table/>` tag.  As evidence, we present the SmartWrap analysis of datasets.
  1. Underuse of the `<table/>` tag causes significant problems for screenreader users.  Previous work by [Gunderson and Mendelson](https://gae-wrapup-server.googlecode.com/hg/doc/bib/1997_gundersonUsabilityWWWVizImpaired.pdf) indicates that table lookups without recourse to the _table mode_ (because it had not yet been [invented](https://gae-wrapup-server.googlecode.com/hg/doc/bib/1999_ooganeTableAccessHTML.pdf)) is essentially impossible for screenreader users.  Our study quantifies the impact of rewriting template datasets using the semantics of the `<table/>` tag.
  1. We have a system that automatically performs this rewriting using the contributions of third-party volunteers.  The system addresses the following main problems:
    1. recognizing pages which volunteers have already wrapped, also logging failed attempts to re-write a page so that volunteers will wrap it in future
    1. a button to request re-writing; via this button a screenreader indicates that the page contains a dataset that does not use the `<table/>` tag, but should
    1. accurate re-writing of the dataset using a `<table/>` tag
    1. a user interface (SmartWrap) allowing nontechnical volunteers to contribute wrappers
  1. Our study provides an estimate for the speedup of table lookups by screenreader users in a `<table/>` dataset versus a layout dataset.  For example, instead of taking 15x as long as a sighted user for a table lookup (a ballpark estimate from the Gunderson and Mendelson paper) in a layout dataset, a screenreader user may take only 3x as long as a sighted user using a `<table/>` dataset with proper semantics.

# Study #

## Objective ##

To quantify the efficiency gains for screenreader users of marking up
datasets with `<table/>` tags, separately analyzing familiar and
novel datasets.

## Hypotheses ##

  1. Experienced Screen Reader Users will be able to answer factual queries about datasets presented as tables faster and more accurately than queries about datasets presented as lists.
  1. Visual Browser Users will be able to answer factual questions about datasets presented as tables faster and more accurately than queries about datasets presented as lists. (we do not expect this difference to be large, in fact it may be swamped by experimental noise)

## Conditions ##

The "full system" would involve automatically transcoding "bad"
template-structured lists/grids into "good" table-structured tables.
For the experiment we do not need to build the full system.  Instead
we just have to build two web applications, both serving the same
data; the first "control" webapp serves datasets as rich visual
templates, and the second "experimental" webapp serves datasets as
tables.  In terms of the full system the "control" application serves
the original untranscoded pages as they exist on the web, and the
"experimental" application serves the results of the transcoding.

In order to measure granularly the difficulties caused by the table
overuse and table underuse problems above, we can measure the
following conditions:

  1. `[raw]` WebApp serving filled templates, specifically original pages from the corpus which we identify to exhibit table underuse and probably also table overuse
  1. `[fix underuse]` WebApp serving datasets without `<table/>` underuse: the same pages re-written with a perfect SmartWrap wrapper, so that all datasets use the `<table/>` tag
  1. `[fix overuse]` WebApp serving datasets with neither `<table/>` underuse nor `<table/>` overuse; we additionally rewrite all `<table/>` tags to `<div/>` which are not identified as datasets by the (perfect) wrapper
  1. `[rich]` WebApp serving rich tables (optional); we add rich `<caption/>` tags and `<th/>` header tags to all dataset tables.

Note that for logical completeness we could test a condition where
`<table/>` tag overuse is corrected without correcting
`<table/>` tag underuse, i.e. re-write all instances of the
`<table/>` tag without inserting any semantic `<table/>` tags.
I think this is not worth testing: this condition is equivalent to a
screenreader user simply never using the _table mode,_ and since users
do use the _table mode_ we can conclude that simply removing it will
not help.  QED.

Most of the websites gather in the SmartWrap work contained datasets
which we can assume our participants have not encountered before.
Additionally we can add a number of more popular sites which we expect
most of our participants to have used in the past, e.g. Zappos or a
particular product query on Amazon.  This will roughly double the
number of conditions (raw-obscure AND raw-popular).  We expect to see
a significant improvement for obscure sites, but for popular sites it
is not clear that the benefits of re-writing will outweigh the costs
of invalidating the users' adaptive browsing strategies.  On popular
sites, if we break even we can probably spin it rhetorically as a win:
even after we turned a popular site into a novel site, the user
accomplished the task in about the same time.  A clear win would, of
course, be even better.

## Experiment Design ##

Each participant will be consented -- if we're working with blind
people we probably would want to do this electronically, IRB
permitting.

Then each participant will be asked to complete 2k-4k tasks (where k
is the number of conditions we run), with the condition orderings
counterbalanced.  The condition is the independent variable, while
participant ID and the task contribute random effects to the
measurement (basically we expect some users to be faster than others,
and some tasks to be easier than others, but we do not care to measure
these effects, only to account for them in the analysis).

An additional potential random effect is task ordering, i.e. we would
expect people to speed up as they get comfortable with the tasks, slow
down as they get tired, etc.  Additionally, people's performance may
be influenced by whether they see popular or obscure sites initially.
To account for this, we will additionally counterbalance the ordering
of tasks.

More explicitly: Given task websites A, B, C, D, and so on, we will
generate versions

  1. A(R) A as a raw website
  1. A(FU) A with instances of table underuse fixed
  1. A(FO) A with instances of table underuse AND table overuse fixed

We also cluster the tasks in blocks: A,B; C,D; E,F; etc, arranging so
that about half the blocks begin with popular sites.  Based on
orthogonal latin squares for the two axes of ordering (block ordering
and condition ordering) we arrive at the following order of tasks:

  1. Participant 1 sees C(FO)-D(FO)-E(FU)-F(FU)-A(R)-B(R)
  1. Participant 2 sees A(FU)-B(FU)-C(R)-D(R)-E(FO)-F(FO)
  1. Participant 3 sees E(R)-F(R)-A(FO)-B(FO)-C(FU)-D(FU)
  1. Participant 4 sees same ordering as Participant 1, and so on.

The dependent measurements are time to completion and correctness of
response.

## Tasks ##

Each simulated "website" the participant visits exposes a database.
Task completion is equivalent to collecting a small number of values
from this database based on a small number of criteria.

Concretely, we are asking the participant to be the query processor
for a simple "lookup" query of the form

```
select field1, field2 from table1 where criterion1 and criterion2
```

Each task consists of a particular website and a query relating to
that website.  The condition alters how the website presents the
table(s) to the participant, and thus impacts the participant's
strategy for retrieving the information.

The result set should be of manageable size.  Ideally it will have at
most a couple of columns, and exactly one row or zero rows.

Each website will be presented to the participant starting from an
(accessible!)  web form -- the draft form is
[here](http://gae-wrapup-server.appspot.com/ViewAccessTask.jsp).  The
web form will contain

  1. a concise unambiguous description of the criteria and the desired field(s)
  1. a link to the website containing the answer
  1. a field where the participant should paste the answer to the query
  1. a submit button

We can gather consistent timing information by instrumenting the web
forms.  Specifically, the time to completion is the time from when the
form is loaded (or when the link is followed) until the time when the
submit button is activated.

Since the instrumentation is in a web form, we do not particularly
care what browser and screen reader participants use.  Ideally,
participants would use whatever combination they are most comfortable
with.  We could provide a study laptop running Windows with the most
popular screenreaders loaded (with vanilla configurations), and we
could also allow participants to use their own devices if they
preferred that.

|num|question|raw|fix underuse|fix overuse|
|:--|:-------|:--|:-----------|:----------|
|1 |What is the phone number of the office in the Mint House?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57359959326720000.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57359959326720001.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57359959326720003.xhtml)|
|2 |What is the phone number of a plumber in Rose Bay?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/48163231755141120.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/48163231755141121.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/48163231755141123.xhtml)|
|3 |What company is looking for a Marketing/PR job that is not an internship position?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/51304672840908800.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/51304672840908801.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/51304672840908803.xhtml)|
|4 |Find the address of a doctor with over 40 years of experience|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/56427790362214400.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/56427790362214401.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/56427790362214403.xhtml)|
|5 |Find the phone number of a florist in Wareham|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/62649496093327360.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/62649496093327361.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/62649496093327363.xhtml)|
|6 |What is the phone extension for a psychic who uses Tarot and specializes in Past Lives?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/56762016664125440.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/56762016664125441.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/56762016664125443.xhtml)|
|7 |What is the phone number of an attorney in Lake Oswego?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57507904843939840.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57507904843939841.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57507904843939843.xhtml)|
|8 |Who posted a document that has been signed more than 180 times?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/61264089967493120.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/61264089967493121.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/61264089967493123.xhtml)|
|9 |Find the name of a supporter who has backed more than 75 other projects.|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/56360268107612160.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/56360268107612161.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/56360268107612163.xhtml)|
|10|How many crematoria are listed in West Sussex?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/52877186532638720.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/52877186532638721.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/52877186532638723.xhtml)|
|11|Which phones (if any) receive a rating of 4.5 out of 5 possible points?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57097029481922560.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57097029481922561.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57097029481922563.xhtml)|
|12|What is the price associated with a deal with over 40 available offers?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/45838030413496320.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/45838030413496321.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/45838030413496323.xhtml)|
|13|How many tracks in the playlist are longer than 4 minutes?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57495633317068800.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57495633317068801.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57495633317068803.xhtml)|
|14|What is the purchase date of the property larger than 1000 square meters?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57542581336145920.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57542581336145921.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57542581336145923.xhtml)|
|15|What is the address of the office in Ascot?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57159215239659520.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57159215239659521.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57159215239659523.xhtml)|
|16|What is the name of a company with over 400 follows?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/62398393514721280.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/62398393514721281.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/62398393514721283.xhtml)|
|17|How many events are scheduled in 2015 in California?|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/51390497024901120.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/51390497024901121.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/51390497024901123.xhtml)|
|18|Find the name of a player who threw lefthanded and was born in Louisiana|[raw](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57500850360156160.xhtml)|[FU](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57500850360156161.xhtml)|[FO](http://1412-dot-gae-wrapup-server.appspot.com/atasks/57500850360156163.xhtml)|


## Piloting ##

There are a couple of wrinkles we will want to work out.

First we need to know that the tasks we want to use have clear and
unambiguous answers.  I think we can build the study infrastructure,
then pilot with sighted and/or remote users to make sure we're getting
the answers we expect.  This will also allow us to shake out problems
with the timing mechanism.  Lastly, this piloting will give us some
hint as to whether or not sighted users are aided by `<table/>` tags
(hypothesis 2 above).

Second we need to know that the task forms and the study
infrastructure itself are accessible.  In addition to applying best
practices in their implementation, we'll probably want to pilot with a
couple blind screenreader users to make sure nothing is biasing our
measurements.