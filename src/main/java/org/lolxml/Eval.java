package org.lolxml;

import java.io.Writer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Eval extends GrammarNode {

	protected Eval(Node xmlNode) {
		super(xmlNode);
	}

	@Override
	void eval(Writer out) {
		GrammarNode gnRef=getGrammar().getReference(((Element)xmlNode).getAttribute(ATT_IDREF));
		if (gnRef!=null)
			gnRef.eval(out);
	}

}
