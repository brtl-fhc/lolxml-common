package org.lolxml;

import java.io.StringWriter;
import java.io.Writer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Store extends GrammarNode {
	
	String property;
	String idRef;
	
	protected Store(Node xmlNode) {
		super(xmlNode);
		this.property=getNodeAttribute(ATT_PROPERTY);
		this.idRef=getNodeAttribute(ATT_IDREF);
		mixed=true;
	}
	
	/** Silent eval, stores Exp call result or nested content in property */
	@Override
	void eval(Writer out) {
		if (idRef!=null){
			storeRef();
		}else{
			storeContent();
		}
	}
	
	private void storeRef(){
		GrammarNode gnRef=getGrammar().getReference(((Element)xmlNode).getAttribute(ATT_IDREF));
		if (TAG_EXP.equals(gnRef.xmlNode.getLocalName())){
			getGrammar().putProperty(this.property,((Exp)gnRef).call());
		}
	}
	
	private void storeContent(){
		StringWriter sw=new StringWriter();
		super.eval(sw);
		getGrammar().putProperty(this.property, sw.toString());
	}

}
