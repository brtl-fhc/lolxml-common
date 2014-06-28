package org.lolxml.node;

import org.w3c.dom.Node;

/** Non-terminal symbol. Evaluates its content when referenced. */
public class Sym extends GrammarNode {

	protected Sym(Node xmlNode) {
		super(xmlNode);
		mixed=true;
	}

}
