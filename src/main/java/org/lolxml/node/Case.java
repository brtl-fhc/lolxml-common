package org.lolxml.node;

import org.w3c.dom.Node;

/** Child of a switch node. Gets selected randomly. */
public class Case extends GrammarNode {

	protected Case(Node xmlNode) {
		super(xmlNode);
		mixed=true;
	}
}
