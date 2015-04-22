# Architecture #

The following sequence diagram shows the role of the [wrapper](WhatIsaWrapper.md) server in
rewriting a user's page

![http://gae-wrapup-server.googlecode.com/hg/doc/arch/plugin.png](http://gae-wrapup-server.googlecode.com/hg/doc/arch/plugin.png)

In words:

The server works in tandem with
[a Client Plugin](http://code.google.com/p/wrapup-client) (currently
implemented only for Firefox).  The plugin listens for pageloads, and
when a new page is loaded it queries the server to see if the server
has a wrapper for the page.

The client begins by [querying by URL](QueryByURL.md).  If the server has a wrapper
that matches the URL of the document exactly, then the wrapper is
returned.

If no wrapper was retrieved by the document URL, the client queries
the server for wrappers by the document's DocumentSignature.  Since
the document may change over time (e.g. a result set inserted into the
document) the client may re-query by the DocumentSignature several
times.

If a wrapper is returned by either query, then the wrapper is applied
to the document in the browser.  Applying the wrapper to the document
merely inserts structured meta-information
(e.g. [microdata](http://schema.org/docs/datamodel.html) or
[RDFa Lite](http://www.w3.org/TR/rdfa-lite/)).  Without further
intervention, this would generally not affect the user at all; the
meta-information would be applied invisibly.  The plugin, however, is
conceptually part of a larger system that does something with the
applied meta-information.  For example the
[MIXER tool](https://gae-wrapup-server.googlecode.com/hg/doc/bib/2011_gardinerMixer.pdf)
understands that microdata-enriched pages can be used to build out
programming-by-demonstration data retrieval queries (see also MixerSmartwrapIntegration).  The plugin could
also be packaged with an AccessibilityPlugin that knows how to
re-write microdata sequences as simple HTML tables, which would
presumably be more accessible.

# Prototype Implementations #

Prototype implementations of the
[server](https://code.google.com/p/gae-wrapup-server/) and the
[plugin](https://code.google.com/p/wrapup-client/) exist on google
code.  These prototype implementations can be extended as part of a
more complete implementation, or can be used as inspiration for the
implementation.

The prototype implementation of the server (hosted on
[Google App Engine](http://gae-wrapup-server.appspot.com/)) contains
wrappers for a single page:
http://gae-wrapup-server.appspot.com/test/hoover.xhtml -- this page
has a simple data model but lots of complicating browser behavior such
as ads loading.

HowToContribute