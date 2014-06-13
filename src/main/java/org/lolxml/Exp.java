package org.lolxml;

import java.io.Writer;

import org.w3c.dom.Node;

public class Exp extends GrammarNode {

	String value;
	protected Exp(Node xmlNode) {
		super(xmlNode);
		mixed=true;
	}
	
	@Override
	void eval(Writer out) {
		
	}

}
