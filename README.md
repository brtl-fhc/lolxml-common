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
the XML source. 

```java
	Grammar root=LolXML.load(inputStream, false); // Disabling schema validation
```
Subsequent calls to doGenerate() on the Grammar object will 
render an evaluation of the grammar to an output stream.

```java
	root.doGenerate(outputStream);
```
The LolXML grammar specification schema
=======================================

A LolXML file specifies a context-free generative grammar, a group of 
production rules which define how non-terminal symbols on the left (named tokens
which do not appear in the final output) are expanded into a combination
of terminal (final output) or other non-terminal symbols, which are then
expanded again until the output text (formed by terminal symbols only) is
obtained. 

	<SIGN> -> [+-*/]
	<DIGIT> -> [0-9]
	<NUMBER> ->  <DIGIT> | <DIGIT><NUMBER>
	<EXPRESSION> -> <NUMBER> | <NUMBER><SIGN><EXPRESSION> | (<EXPRESSION>)

In the above grammar, the root symbol EXPRESSION can produce strings
like "2", "65+1", "(1*100/2)" or "(100-(50+25))".

Basics: non-terminal symbols (sym) and random choices (switch/case)
-------------------------------------------------------------------


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