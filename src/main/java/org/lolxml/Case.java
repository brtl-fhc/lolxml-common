package org.lolxml;

import org.w3c.dom.Node;

public class Case extends GrammarNode {

	protected Case(Node xmlNode) {
		super(xmlNode);
		mixed=true;
	}
}
