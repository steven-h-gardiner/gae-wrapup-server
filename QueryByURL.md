The simplest lookup for a page is simply to lookup the URL.  If a wrapper was stored with exactly the same URL as the page, then chances are good that the wrapper can be applied to the page.

In many cases, however, a large number of URLs (e.g. different query strings, different anchors, proxy servers, etc) will map to the same underlying page.  Less often, the same template might be served at multiple URLs.  Many but not all of these should be addressed by the URL query service. [Issue#1](https://code.google.com/p/gae-wrapup-server/issues/detail?id=#1) is to expand the URL query beyond exact string matching.

# Examples #