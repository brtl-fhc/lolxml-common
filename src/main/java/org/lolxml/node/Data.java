package org.lolxml.node;

import java.io.Writer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Data extends GrammarNode {

	protected Data(Node xmlNode) {
		super(xmlNode);
		getGrammar().setData((Element)xmlNode);
	}

	@Override
	public
	void eval(Writer out) {
		// Can't execute data. Do nothing
	}
	
}
