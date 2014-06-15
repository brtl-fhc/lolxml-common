package org.lolxml;

import java.io.Writer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Store extends GrammarNode {
	
	String property;
	
	protected Store(Node xmlNode) {
		super(xmlNode);
		this.property=((Element)xmlNode).getAttribute(ATT_PROPERTY);
	}
	
	/** Silent eval, stores Exp call result in property */
	@Override
	void eval(Writer out) {
		GrammarNode gnRef=getGrammar().getReference(((Element)xmlNode).getAttribute(ATT_IDREF));
		if (TAG_EXP.equals(gnRef.xmlNode.getLocalName())){
			getGrammar().putProperty(this.property,((Exp)gnRef).call());
		}
	}


}
