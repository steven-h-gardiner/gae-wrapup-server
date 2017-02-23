
Information on web pages is coded in HTML, and browsers accept a lot
of crap in the actual HTML presented.

The original pages wrapped by SmartWrap users was saved to a
repository by the SmartWrap tool.  Rather than save the original HTML,
which may not be interpretable by the tools involved, SmartWrap saved
the rendered DOM as the user's Firefox browser understood it.

In general it is very nontrivial to recover the original HTML and its
appearance and behavior from the saved DOM.  It might be possible
manually to approximate the original appearance.

Downstream from the saved DOM several additional transformations are
applied.

1. clear.xsl removes markup that the SmartWrap tool inadvertantly
added.  In some cases this markup may assist screen reader users in
the unimproved condition.

2. rez.xsl points resources (e.g. images, stylesheets, etc) to local
cached versions rather than their original location out on the web.

3. general.xsl removes elements from the page that caused crashing
and/or long load times, and despite its name has lots of quite custom
XPaths that are highly specific to individual task pages.

4. underuse.xsl corrects the problem of underuse of tables, i.e. it
injects table structure into a data set that should have used it but
didn't.

5. overuse.xsl corrects the problem of overuse of tables, i.e. it
removes all tables which are not containers of data sets.

The control condition stops after general.xsl.  The experimental
(entable) condition stops after overuse.xsl.  This process is
summarized in the DOT file accompanying this file.

My first guess would be that the original DOM saved by SmartWrap is
already difficult to make look like the original page out on the web,
i.e. to make them look like reasonable pages for sighted users.  I
assert, however, that the transformed pages sound reasonable to screen
reader users.  Any validation of this assertion should probably
involve asking screen reader users.