LolXML
======

A Java library to generate rule-based (possibly) random text defined in XML.
 
Intends to provide a powerful, flexible framework for automated parody (think
generators of band names, corporate-speak or Dan Brown plots).

Usage
=====

Requirements
------------
The library has no dependencies outside the Java runtime library. To build the 
library, the only requirements are Maven and JDK 1.6+.

It will run on:

- JRE 1.6 or later.
- Android 2.2 or later (optional schema validation might not be available on some
versions).


How to build
------------

To build LolXML with Maven:

	mvn package

If the build is successful, find the generated archive 
on test/lolxml-common-<Version>.jar


Command line
------------

The jar is runnable, try it with the provided examples or the unit tests.

	java -jar target/lolxml-common-<version>.jar src/test/resources/if-test.xml 

API
---

The entry point of the library is the LolXML class. Use LolXML.load() to parse
the XML source. Subsequent calls to doGenerate() on the Grammar object will 
render an evaluation of the grammar to an output stream.

	Grammar root=LolXML.load(inputStream, false); // Disabling schema validation
	root.doGenerate(outputStream);

The LolXML grammar specification schema
=======================================

The basics: non-terminal symbols (sym) and random choices (switch/case)
-----------------------------------------------------------------------

XPath Expressions (exp) and the data node 
-----------------------------------------

XPath Extension function
------------------------

Properties (store) and data types
---------------------------------

Procedural control: if, foreach and while
-----------------------------------------

HTML output
-----------

License
=======

LolXML is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).