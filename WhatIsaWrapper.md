# First Approximation #

To a first approximation, a wrapper is JSON stored as a string in the
server's repository; the server can remain agnostic about the
internals and trust that whosoever put the wrapper in the repository
did the right thing so that the requesting client can do the right
thing to inject structured markup in the document.

# Second Approximation #

A wrapper is a JSON object describing how to identify the parts of the
[Universal Nested Relation](https://gae-wrapup-server.googlecode.com/hg/doc/bib/1990_leveneNestedUniversalRelation.pdf)
encoded in the page.  The wrapper is a JSON array modeling the schema
forest of the page.  Often, the array will contain a single object
describing the single relation on the page (a schema tree).  Each
schema tree object contains
  1. a [selector](#selector.md),
  1. a list of `attributes` which describe how to locate the atomic attributes of the tuple, and
  1. a list of nested `children` trees which recursively describe tuples nested under the current tuple.

# selector #

A selector may be an xpath expression or a css selector.

# An example wrapper #

```
 1[
 2  { "name": "person",
 3    "type": "http://schema.org/Person",
 4    "xpathSelector": "//_:table//_:tr[_:td/@width='40%']",
 5    "attributes": {
 6      "name": {
 7        "xpathSelector": "./_:td[@width='40%']",
 8      },
 9      "jobTitle": {
10        "xpathSelector": "./_:td[preceding-sibling::_:td[1]/@width='40%']",
11      },
12    },
13    "children": [],
14  }
15]
```

## Line 1 ##

Note that the wrapper is a list.  This example has a single element
because it wraps a page containing a single relation.  For multiple
relations, the wrapper would have multiple entries, one entry for each
relation.

## Line 2 ##

This is the informal name of the tuple.  This could easily be exactly
what was placed in the table header in SmartWrap.

## Line 3 ##

This is a reference to a formal type for the tuple in some hierarchy.
This example uses the type hierarchy at schema.org.  Currently
SmartWrap has no mechanism for eliciting this type information from
users.

## Line 4 ##

A selector for the element containing the tuple.  XPath selectors have
a weird situation with namespaces discussed in [issue#2](https://code.google.com/p/gae-wrapup-server/issues/detail?id=#2).

## Line 5 ##

A list of attributes, each having a name and a selector telling how to
locate the value of the attribute.  XPath selectors can reference an
attribute outside of the tuple's subtree,
e.g. "`./following-sibling::_:p[1]`"

## Line 13 ##

If the relation is nested, the list of children will contain a wrapper
for each nested tuple.  Each nested wrapper will recursively have the
same format as described here.