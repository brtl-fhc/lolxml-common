LolXML
======

A Java library to generate rule-based random text defined specified in XML.
 
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
- Android 2.2 or later (optional schema validation might not be available 
on some versions).


How to build
------------

To build LolXML with Maven:

	mvn package

If the build is successful, find the generated archive 
on test/lolxml-common-<Version>.jar


Command line
------------

The jar is runnable, it's recommended to try it with the provided examples 
or the unit tests.

	java -jar target/lolxml-common-<version>.jar src/test/resources/if-test.xml 

API
---

The entry point of the library is the LolXML class. Use LolXML.load() to parse
the XML source. 

```java
	Grammar root=LolXML.load(inputStream, true);
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

	<SIGN> 	-> + | - | * | /
	<DIGIT> -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
	<NUMBER> ->  <DIGIT> | <DIGIT><NUMBER>
	<EXPRESSION> -> <NUMBER> | <NUMBER><SIGN><EXPRESSION> | (<EXPRESSION>)

In the above grammar, the root symbol EXPRESSION can be expanded into strings
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
Here are the most basic elements of a LolXML file:

- A grammar root element, which wraps the whole document.
- A data element (empty at the moment, but required by the schema).
- Several sym elements, which declare how each non-terminal symbol is
*evaluated* (expanded and written to the output stream). A sym element 
has a unique id and can contain text (terminal symbols) or special 
elements, which are evaluated recursively. It's a *mixed content* node.
- A switch element represents a random choice in the evaluation (the '|'
in the formal notation). It can only contain case elements. When the 
runtime finds a switch, it will randomly pick one of the case nodes and 
evaluate just that one.
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
move some of it into the data node, which allows any structured data as long 
as it's valid XML.

```xml
	<data>
		<signs xmlns="">
			<sign>+</sign> <sign>-</sign>
			<sign>*</sign> <sign>/</sign>
		</signs>
	</data>
```
The xmlns="" attribute is needed for the data elements to be local. It resets the
default namespace to "no namespace". An alternative to doing this is having a prefix
for the LolXML namespace. Both alternatives are used in the example grammars and unit
tests.

Data is selected from, exp elements, which declare XPath expressions. 
The XPath string found in the value attribute will be parsed
and run using data as context (i.e. node paths are relative to data). 

We can rewrite the SIGN rule as:

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

Properties (store) and data types
---------------------------------

Normally, the document is explored, top-down, from the root element. When an
evaluable node is found, it's expanded immediately to the output stream.

Sometimes, though, it's convenient to reuse the result of a previously
evaluated rule. For example, a selected person name that needs to appear 
throughout the whole output document.

Unlike XForms instances, LolXML grammars and data are immutable. That means
the structured data under the data element will be identical for each 
execution, and cannot be modified.

It's possible, to keep values in properties. A property is a named variable
which can store values of any XPath type (string, number, boolean, node 
or node-set). A property is created on its first use, and its value can
be reset at any time.

The store element allows keeping values in properties. The name of the
property to be set is specified by the property attribute. There are several
ways to select the value to be set:

- If a select attribute is present, its content is run as XPath. The store
element allows choosing a type for the value to be converted to, if needed,
before setting the property (string, boolean, node, nodeset, number). The
default type is string.
- If an idref attribute is present, the referenced node (usually a sym or
exp) is located and evaluated, not writing the result to the program output,
but capturing it, converted to the selected type and finally stored.
- If none of the above attributes are present, the nested mixed content is
evaluated and captured, converted to the selected type and stored.

To recall the value of a property, the eval element uses the property attribute
(alternative to idref, which is used for evaluable nodes). 


Examples:

```xml
	<lol:store property="class"><lol:eval idref="CLASS"/></lol:store>
	<lol:eval property="class"/>
```
The excerpt above sets the result of the CLASS rule into the 'class' property,
and then writes it.

Properties are also exposed like XPath variables: $foo in an XPath attribute 
will be evaluated as the value kept in the "foo" property.


```xml
	<lol:store property="bottles" type="number" select="start"/>

	<lol:store property="bottles" select="$bottles - 1" type="number" />
```
The value of the start data node is put in the property "bottles", and it's 
then decreased by 1.


XPath extension functions
-------------------------

LolXML defines two extension functions for XPath.

*random()*

The lol:random() function is an XPath extension provided by LolXML. It provides
three modes of use:

- lol:random(): returns a floating-point number between 0.0 and 1.0.
- lol:random(i): returns an integer between 0 (inclusive) and i (exclusive).
- lol:random(nodeset): returns a random node from the set.

We made our grammar simpler using lol:random(10) to generate digits, and 
lol:random(signs/sign) to pick the random sign (in replacement of the heavier 
switch/case structure).


*evaluate()*

The function, lol:evaluate(string), expands evaluable nodes (referenced 
by their id attribute) from an XPath expression. For example:

```xml
	<exp value="normalize-space(lol:evaluate('TXT'))" />
```
The above command would search the document for an evaluable element whose
id is TXT, evaluate it and normalize its whitespace before it's written to the
output stream.


Procedural control: if, foreach and while
-----------------------------------------

Some constructions are easier to express using imperative statements than
recursive grammar rules. For example, it's convenient to iterate a list 
of values without recursion, or slightly altering the output depending of 
some property (selecting a pronoun or verbal form).

- if: Conditional evaluator. Evaluates its nested content if its test
condition is true. Test condition may be specified by inline boolean XPath 
(test attribute), referencing an Exp (idref) or recalling a runtime 
property (property).

	<lol:if test="$bottles > 2"><lol:eval idref="VERSES"/></lol:if></lol:sym>
	
- foreach: Nodeset iterator. Evaluates nested content one time for each node
in the set, exposing the current node as a runtime property (declared by the
var attribute). The nodeset to iterate may be selected using inline XPath 
(select attribute), referencing an Exp (idref) or recalling a runtime property 
(property).

```xml
   <foreach select="farm/animal" var="animal">
	    <store property="animalSound" select="$animal/sound"/>
	    <store property="sound.x2"><eval property="animalSound"/>-<eval property="animalSound"/></store>
	    Old MacDonald had a farm, E-I-E-I-O,
	    And on that farm he had a <exp value="$animal/@name"/>, E-I-E-I-O,
	    With a <eval property="sound.x2"/> here and a <eval property="sound.x2"/> there
	    Here a <eval property="animalSound"/>, there a <eval property="animalSound"/>, everywhere a <eval property="sound.x2"/>
	    Old MacDonald had a farm, E-I-E-I-O. 
    </foreach>

```

- while: Conditional loop. Evaluates its nested content while the test condition
is true. Condition may be specified by inline boolean XPath (test attribute), 
by a referenced boolean Exp (idref attribute) or recalling a runtime property 
(property attribute). 

```xml
<while idref="tableInRange">
	<store property="j" type="number" select="1"/>
	<while test="$j &lt; 11">
		<exp value="$i"/> x <exp value="$j"/> = <exp value="$i * $j"/>
		<store property="j" type="number" select="$j+1" />
	</while>
	<store property="i" type="number" select="$i+1" />           
</while>
```

HTML output, whitespace and layout
----------------------------------

The nested content of LolXML elements admits foreign markup (elements from other
namespaces, or without one) in the text. Those foreign elements will be simply
rewritten to the output stream, allowing the generation of HTML contents.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<lol:grammar xmlns:lol="http://lolxml.org" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://lolxml.org ../../main/resources/lolxml.xsd ">
  
  <lol:data>
  	<name>World</name>
  </lol:data>
  
  <lol:sym id="ROOT">
    <html xmlns="http://www.w3.org/1999/xhtml">
    	<body>
    		<p class="cl1">Hello <b><lol:exp value="name"/>!</b></p>
    	</body>
    </html>
  </lol:sym>
  
  <lol:eval idref="ROOT"/>
</lol:grammar>
```

In the LolXML grammar processing, whitespace is preserved and written to the output stream. If
the XML is "pretty-print" indented, output text can have unwanted whitespace at the beginning
of lines.

Some tricks for avoiding the unwanted whitespace problem:

- Manually indent the file so text appears in the exact column it's wanted in the output.
- Use the normalize-space on evaluation results to trim the namespace. 
- Render the output as HTML and use its format capabilities. This makes the layout of
rendered text independent of the indentation of the source XML. 


License
=======

LolXML is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).
