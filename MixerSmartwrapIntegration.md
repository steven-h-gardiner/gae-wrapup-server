# Changes to MIXER #

The previous version of MIXER had a handful of wrappers hard-coded in its codebase.  When run, the wrappers would inject HTML `class` information into the viewed document which MIXER would then highlight and instrument to allow the elements to be placed in the workspace.

The current version of MIXER (a work in progress) does not need the `class` information, since it is functionally equivalent to the microdata injected by the [plugin](https://code.google.com/p/wrapup-client).  Instead the MIXER plugin just needs to be installed alongside the wrapper client plugin.  Whenever microdata and/or RDFa Lite appears on the page (including initially, for pages that already have embedded machine-readable markup), MIXER will automatically be able to add it to the workspace and start creating a script.

# Changes to SmartWrap #

The previous version of SmartWrap computes an XPath to detect tuple elements, and an XPath for each field of the tuple.  It should be relatively easy to reformat the exported wrapper into the format expected by the [wrapper server](MainPage.md).

