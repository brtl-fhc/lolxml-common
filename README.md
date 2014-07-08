
LolXML
======

A Java library to generate rule-based random text defined specified in 
XML. It intends to provide a powerful, flexible framework for automated 
comedy (think generators of band names, corporate-speak or Dan Brown plots).

Usage
=====

Requirements
------------
The library has no dependencies outside the Java runtime library. It will run on:

- Java desktop: JRE 1.6 or later.
- Android 2.2 or later (optional schema validation might not be available 
on some versions).

You will also need Maven to build the library (tested on version 3).

Building the library
--------------------

To compile and package a LolXML binary with Maven:

	mvn package

If the build is successful, find the generated archive 
on test/lolxml-common-&lt;version&gt;.jar


Command line
------------

The output artifact is standalone runnable. it's recommended to try it with the 
provided examples or the unit tests.

	java -jar target/lolxml-common-&lt;version&gt;.jar examples/math-basic.xml 

API
---

The entry point of the library is the LolXML class. First, Use __LolXML.load()__ to parse
the XML source.

```java
	Grammar root=LolXML.load(inputStream, true);
```

Subsequent calls to __doGenerate()__ on the Grammar object will 
print the result of evaluating the grammar to an output stream.

```java
	root.doGenerate(outputStream);
```

For more information on the API, generate the Javadoc with Maven.

	mvn javadoc:javadoc
	
And find it under the _target/site/apidocs_ subdirectory.

Writing LolXML grammars 
=======================

The LolXML schema
-----------------

The LolXML schema is found in the source tree `src/main/resources/lolxml.xsd`. An editor
with schema validation and autocompleting features is highly recommended for editing
grammars. XML-enabled editors (such as Eclipse) can also generate basic document
instances.

A basic grammar looks like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<grammar xmlns="http://lolxml.org" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xsi:schemaLocation="http://lolxml.org http://lolxml.org ">

	<data> <!-- Data would go here --> </data>

	<!-- Production rule(s): -->

	<sym id="HELLO">Hello World!</sym>   


	<!-- Evaluate root production: -->

	<eval idref="HELLO"/>  

</grammar>
```

Runtime schema validation is optional, as it may not be supported in some versions of
Android. Schema validation won't prevent non-passing documents from being loaded, it'll 
just print any errors on stderr.


Introduction to context-free grammars
-------------------------------------

A LolXML file specifies a [context-free generative grammar]
(http://en.wikipedia.org/wiki/Context-free_grammar). It has 
production rules which define how each non-terminal symbol on 
its left side (named tokens which do not appear in the final output) 
are expanded into a combination of terminal (final output) or other 
non-terminal symbols, which are then expanded again until the output 
text (where only terminal symbols remain) is obtained. 

A simple grammar for arithmetic expressions with natural numbers, in pseudo-BNF notation:

	<SIGN> ::= + | - | * | /
	<DIGIT> ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
	<NUMBER> ::=  <DIGIT> | <DIGIT><NUMBER>
	<EXPRESSION> ::= <NUMBER> | <NUMBER><SIGN><EXPRESSION> | (<EXPRESSION>)

In the above grammar, the root non-terminal symbol EXPRESSION can be expanded into strings
like "2", "65+1", "(1\*100/2)" or "(100-(50+25))".


Basics: non-terminal symbols (sym) and random choices (switch/case)
-------------------------------------------------------------------

The following is a direct LolXML translation of the example grammar:

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

Note than each production rule has its own `sym` element, with an unique _id_. Within the 
right part of the rule, a `switch` element contains `case` children, with alternative
expansion patterns.

The basic building blocks of a LolXML file are:

- A `grammar` root element, which wraps the whole document.
- A `data` element (empty in this case, but mandatory in the schema).
- One or more `sym` elements, which declare how each non-terminal symbol is
*evaluated* (expanded and written to the output stream). A `sym` element 
has a unique id and can contain text (terminal symbols) or schema tags 
elements, which will be evaluated recursively. `sym` nodes are *mixed content*.
- A `switch` element represents a random choice in the evaluation (the disjunctive '|'
in the formal notation). It can only contain `case` elements. When the 
runtime finds a `switch`, it will randomly pick one of the `case` nodes and 
evaluate just that one.
- The `case` element is mixed-content as well (it allows text and other
special tags).
- The `eval` node can be found in mixed-content nodes and is a reference to an
evaluable element. When the runtime finds an `eval` node, it will search 
for the element whose _id_ matches the idref attribute), and writes the result
of its evaluation to the output stream.

At the end of the file is the top-level `eval` node, which points to the
non-terminal symbol which represents the grammar's _root production_ (in this
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

In the example provided by the previous section, the rule with the all the digits 
looks ugly. We have also literal constants (digits, signs) mixed with the grammar 
rules, which can be hard to keep track of if the file grows larger. We can move
text constants into the `data` node, which allows any structured data as long 
as it's valid XML.

```xml
	<data>
		<signs xmlns="">
			<sign>+</sign> <sign>-</sign>
			<sign>*</sign> <sign>/</sign>
		</signs>
	</data>
```

The `xmlns=""` attribute means the data elements are _local_: the default namespace is 
set to "no namespace". An alternative to doing this is having a prefix for the LolXML 
tags, and using no prefix inside `data`. Both styles are present in the example
grammars and unit tests.

Data is selected using `exp` elements, which wrap [XPath](http://www.w3.org/TR/xpath/) 
expressions. The XPath string found in the _value_ attribute will be parsed
and run using the `data` node as context (i.e. node paths will be assumed as 
relative to `data`). 

We can rewrite the SIGN rule as:

```xml
	<sym id="SIGN"><exp value="lol:random(signs/sign)" /></sym>
```

Note that the `exp` above is inline (defined inside the element). An `exp` found in
mixed content will be evaluated as a string and written, immediately. 

If, on the other hand, the `exp` is declared as a top-level element, it won't be run
automatically. In that case it's expected to have an _id_ attribute, and can be called 
from an `eval` (as is the case with `sym`'s).

We can replace the old DIGIT rule with a concise top-level `exp`:

```xml
	<exp id="PickDigit" value="lol:random(10)" />
	
	<sym id="DIGIT"><eval idref="PickDigit" /></sym>
```

In this case, the `exp` is declared outside the `eval`, at the top level, and it has 
an _id_ to be referenced by `eval` elements. This allows complex XPath expressions to
be organized and reused as the author sees convenient.

Properties (store) and data types
---------------------------------

Normally, the document is explored, top-down, from the root element. When an
evaluable node is found, it's expanded immediately to the output stream.

Sometimes, though, it's convenient to reuse the result of a previously
evaluated rule. For example, a selected person name needs to appear 
throughout the whole output document.

Unlike XForms model instances, LolXML data are immutable. That means
the structured data under the `data` element will be identical for each 
execution, and cannot be modified.

It's possible to keep values in _runtime properties_. A _property_ is a named
variable that "lives" outside the XML document. Properties can store values of any 
XPath type (string, number, boolean, node or node-set). A property is created on 
its first use, and its value can be reset at any time.

The `store` element allows keeping values in properties. The name of the
property to be set is specified by the _property_ attribute. There are several
ways to select the value to use in a `store`element:

- If a _select_ attribute is present, its content is run as XPath. The `store`
element allows choosing a _type_ for the value to be converted to, if needed,
before setting the property (string, boolean, node, nodeset, number). The
default _type_ is "string".
- If an _idref_ attribute is present, the referenced node (usually a `sym` or
`exp`) is located and evaluated, not writing the result to the program output,
but capturing it, converted to the selected _type_ and finally stored in the 
specified _property_.
- If none of the above attributes are present, the nested mixed content is
evaluated and captured, converted to the selected _type_ and stored in the 
specified _property_.

To recall the value of a property, the `eval` element uses the _property_ attribute
(alternative to _idref_, which is used for calling evaluable nodes). 


Examples:

```xml
	<lol:store property="class"><lol:eval idref="CLASS"/></lol:store>
	<lol:eval property="class"/>
```

The excerpt above sets the result of the CLASS rule into the 'class' property,
and then prints it.

Properties are also exposed like XPath variables: $foo in an XPath attribute 
will be printed as the value kept in the "foo" property.


```xml
	<lol:store property="bottles" type="number" select="start"/>

	<lol:store property="bottles" select="$bottles - 1" type="number" />
```

The value of the &lt<start&gt; node under `data` is put in the property "bottles", 
and it's then decreased by 1.


XPath extension functions
-------------------------

LolXML defines two extension functions for XPath.

**random()**

The _random()_ function is an XPath extension provided by LolXML. It provides
three modes of use:

- lol:random(): returns a floating-point number between 0.0 and 1.0.
- lol:random(i): returns an integer between 0 (inclusive) and i (exclusive).
- lol:random(nodeset): picks a random node from the nodeset parameter, and returns it.

We made our grammar simpler using _lol:random(10)_ to generate digits, and 
_random(signs/sign)_ to pick the random operator (in replacement of the syntactically 
heavier `switch`/`case` structure).

**evaluate()**

The function _evaluate(string)_, calls evaluable nodes (referenced 
by their _id_ attribute) from an XPath expression. It works like an &lteval idref="..."/&gt;
in a expression context. For example:

```xml
	<exp value="normalize-space(lol:evaluate('TXT'))" />
```

The above command would search the document for an evaluable element whose
_id_ is "TXT", evaluate it and normalize its whitespace before it's printed to the
output stream.

_evaluate()_ can be passed a string, or a path to a node under `data`. In the latter
case, it will call the element whose id is the text content of the referenced node.  

Procedural control: if, foreach and while
-----------------------------------------

Some constructions are easier to express using imperative statements than
recursive grammar rules. For example, it's convenient to iterate a list 
of values without recursion, or slightly altering the output depending of 
some property (selecting a pronoun or verbal form).

The LolXML schema includes tags for imperative flow control:
 
- `if`: Conditional evaluator. Evaluates its nested content if its _test_
condition is true. Test condition may be specified by inline boolean XPath 
(if a _test_ attribute is present), referencing an `exp` (if _idref_ is present)
 or recalling a runtime property (if _property_ is present).

```xml
	<lol:if test="$bottles > 2"><lol:eval idref="VERSES"/></lol:if></lol:sym>
```

- `foreach`: Nodeset iterator. Evaluates its nested content, one time for each node
in the set. The current node is exposed as a runtime property (declared by the
_var_ attribute). The nodeset to iterate may be selected using inline XPath 
(if the _select_ attribute is present), referencing an `exp` (if _idref_ is present) or
recalling a runtime property (if _property_ is present).

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

- `while`: Conditional loop. Evaluates its nested content while the test condition
is true. Condition may be specified by inline boolean XPath (if a _test_ attribute is
present), by a referenced boolean `exp` (if _idref_ attribute is present) or recalling 
a runtime property (if _property_ is present). 

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
namespaces, or without one) mixed with text. Those foreign elements will be simply
rewritten to the output stream, allowing the generation of HTML content.

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

In the LolXML grammar processing, whitespace is preserved. Every character nested in a
mixed content element will be written to the output stream. This means that, if the XML 
is indented using "pretty-print", the output text can have unwanted whitespace at 
the beginning of each line.

Some tricks for avoiding the unwanted whitespace problem:

- Manually indent the file so text appears in the exact column it's wanted in the 
output. This can require hand-tweaked, non-intuitive format.
- Use the _normalize-space()_ XPath function on the evaluation results, to trim the 
whitespace. 
- Render the output as HTML and use its format capabilities when seen through a browser.
This makes the layout of rendered text unaffected by the indentation of the source XML. 


License
=======

LolXML is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).
