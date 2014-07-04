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

A LolXML file specifies a context-free generative grammar. It has 
production rules which define how each non-terminal symbol on 
its left side (named tokens which do not appear in the final output) 
are expanded into a combination of terminal (final output) or other 
non-terminal symbols, which are then expanded again until the output 
text (where only terminal symbols remain) is obtained. 

	<SIGN> -> [+-*/]
	<DIGIT> -> [0-9]
	<NUMBER> ->  <DIGIT> | <DIGIT><NUMBER>
	<EXPRESSION> -> <NUMBER> | <NUMBER><SIGN><EXPRESSION> | (<EXPRESSION>)

In the above grammar, the root symbol EXPRESSION can produce strings
like "2", "65+1", "(1\*100/2)" or "(100-(50+25))".

Basics: non-terminal symbols (sym) and random choices (switch/case)
-------------------------------------------------------------------

The following is a direct translation of the above grammar to LolXML:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<grammar xmlns="http://lolxml.org" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xsi:schemaLocation="http://lolxml.org http://lolxml.org ">

	<!-- Basic math expressions -->
	<data />

	<sym id="SIGN"><switch>	<case>+</case> <case>-</case>
				<case>*</case> <case>/</case></switch></sym>

	<sym id="DIGIT"><switch><case>0</case> <case>1</case> <case>2</case>
				<case>3</case> <case>4</case> <case>5</case>
				<case>6</case> <case>7</case> <case>8</case>
				<case>9</case>
	</switch></sym>

	<sym id="NUMBER"><switch>
		<case><eval idref="DIGIT"/></case>
		<case><eval idref="DIGIT"/><eval idref="NUMBER"/></case>
	</switch></sym>

	<sym id="EXPRESSION"><switch>
	   <case><eval idref="NUMBER"/></case> 
	   <case><eval idref="NUMBER"/> <eval idref="SIGN"/> <eval idref="EXPRESSION"/></case> 
	   <case>(<eval idref="EXPRESSION"/>)</case> 
	</switch></sym>

	<eval idref="EXPRESSION"/>

</grammar>
```
These are the most basic elements of a LolXML file:

- A grammar root element, which wraps the whole document.
- A data element (empty at the moment, but required by the schema).
- Several sym elements, which declare how each non-terminal symbol is
*evaluated* (expanded and written to the output stream). A sym element 
has a unique id and can contain text (terminal symbols) or special 
elements, which are evaluated recursively. It's a *mixed content* node.
- A switch element represents a random choice in the evaluation. It can
only contain case elements. When the runtime finds a switch, it will 
randomly pick one of its children case nodes and evaluate just that one.
- The case element is mixed-content as well (it allows text and other
special tags).
- The eval node can be found in mixed-content nodes and is a reference to an
evaluable element. When the runtime finds an eval node, it looks everywhere
for the element whose id matches the idref attribute), and writes the result
of its evaluation to the output stream.

At the end of the file is the top-level eval node, which points to the
non-terminal symbol which represents the grammar's root production (in this
case, EXPRESSION).

Some output examples from this grammar:

	(9 + (8 - 232 * 5))
	4 * 3248 * 1359 + (83)
	6 * (268)
	6
	5 / 7
	2 - 8
	34 * (6)
	3 + (885141 + 6655 / 04)
	(4)
	4 + (7 * (9 - (25)))

XPath Expressions (exp) and the data node 
-----------------------------------------

In the example above, the rule with the all the digits looks ugly. We have
also literal constants (digits, signs) mixed with the grammar rules. We can 
move some of it into the data node, which can contain structured data in 
XML format.

```xml
	<data>
		<signs xmlns="">
			<sign>+</sign> <sign>-</sign>
			<sign>*</sign> <sign>/</sign>
		</signs>
	</data>
```

To access the data, we can use the exp element, which declares XPath
expressions. The XPath string found in the value attribute will be parsed
and run using data as context (i.e. node paths are relative to data). 

We can rewrite the non-terminal SIGN as:

```xml
	<sym id="SIGN"><exp value="lol:random(signs/sign)" /></sym>
```
Note that the exp above is inline. An exp found in mixed content will be 
evaluated as string and written immediately. 

If, on the other hand, the exp is declared in the top-level, it's expected 
to have an id attribute and it can be called from an eval (as we do with 
sym's).

We can replace the old DIGIT rule with a concise exp:

```xml
	<exp id="PickDigit" value="lol:random(10)" />
	
	<sym id="DIGIT"><eval idref="PickDigit" /></sym>
```
In this case, the exp is declared at the top level, given an id, and called 
from an eval element. This allows complex XPath to be reused.

The lol:random() function is an XPath extension provided by LolXML. It provides
three modes of use:

- lol:random(): returns a floating-point number between 0.0 and 1.0.
- lol:random(i): returns an integer between 0 (inclusive) and i (exclusive).
- lol:random(nodeset): returns a random node from the set.

We made our grammar simpler using lol:random(10) to generate digits, and 
lol:random(signs/sign) to pick the random sign (in replacement of the heavier 
switch/case structure).

Properties (store) and data types
---------------------------------

Procedural control: if, foreach and while
-----------------------------------------

HTML output
-----------

License
=======

LolXML is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).
