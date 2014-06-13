package org.lolxml;

import org.w3c.dom.Node;

public class Sym extends GrammarNode {

	protected Sym(Node xmlNode) {
		super(xmlNode);
		mixed=true;
	}

}
