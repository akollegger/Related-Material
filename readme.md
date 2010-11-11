Related Materials Project
=========================

Related Materials is a document production project.

Prerequisites
-------------

* [pandoc](http://johnmacfarlane.net/pandoc/)
* [sbt](http://code.google.com/p/simple-build-tool/)

Building
--------

1. prepare - `sbt update`
2. clean - `sbt clean`
3. build pdfs - `sbt pandoc-to-pdf`

Current default behavior is to produce one PDF per source markdown file.
Pending iterations will allow for specficying the set of input files,
and the option of producing a single aggregated document.


