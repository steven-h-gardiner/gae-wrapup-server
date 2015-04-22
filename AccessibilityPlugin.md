# Design #

When invoked, the plugin would scan the page for microdata and/or RDFa
Lite.  Any tuples found would be re-rendered as rows of a simple HTML
table.  The plugin should allow the user to switch back and forth
between the re-rendered page and the original page.

## Invocation ##

The plugin might be invoked by pressing a button, or, equivalently, by
a keystroke.  Alternately, the plugin could listen for an event on the
page and commence operation on receiving that event.  The wrapper
client, for example, could send an event when a wrapper is
successfully applied.  In this scenario, the accessibility plugin
would scan a page upon initial load for any structured tuples, and
then again upon receiving the wrapped event (if any).

# Implementation Questions #

In some cases important information may appear in the tuples that is
not properly marked up in the embedded microdata.  Completely
replacing the tuple's HTML with a TR element would lose information.
The plugin would need a simple rule to deal with this scenario.

# Research Questions #

[Goble et al 2000](https://gae-wrapup-server.googlecode.com/hg/doc/bib/2000_gobleWebTravellers.pdf)
observe that visually impaired web users spend a lot of time and
effort simply understanding where they are in a complicated web page.
Whereas sighted users perceive a lot of context by rapidly changing
focus from the whole page to a small part of it, visually impaired
users change focus more slowly.  In the limiting case, blind users use
a screenreader which has a single cursor (like "looking at the page
through a straw"); blind users must keep all of the context of the
cursor's location in the page in their heads.

[Takagi et al 2007](https://gae-wrapup-server.googlecode.com/hg/doc/bib/2007_takagiNavWebAppsBlindUse.pdf)
analyze web pages based on the number of operations required to move
the screenreader cursor to any given information, predicting that
pages requiring fewer operations will be easier for blind users to
navigate.

The research touched on above suggests that an HTML table will be
easier to use for a visually impaired user than a "pretty" list or
grid.  First, the table would be less confusing since it would
leverage the user's pre-existing familiarity with tables.  Second,
screenreaders supply built-in techniques for navigating "one cell
left/right" or "one cell up/down" within a table, reducing the
navigation time to find the desired information.

## A possible experiment ##

The navigation time argument above suggests a measurable experiment.

We assemble webpages containing structured information marked up as
lists or grids (e.g. transit information).  We ask users (a small
group of sighted users, a small group of visually impaired and/or
screenreader users) to lookup answers to queries within the page
(e.g. what time does bus X arrive at stop Y).  In condition A they see
the original version of the page.  In condition B they see the page
re-rendered as a plain HTML table.  We hypothesize that users in
condition B will spend measurably less time looking up the information
than those in condition A.  We expect the difference to be small for
sighted users but significant for visually impaired users.