package org.lolxml;

import java.io.Writer;

import org.w3c.dom.Node;

public class Eval extends GrammarNode {

	String idRef;
	String property;
	
	protected Eval(Node xmlNode) {
		super(xmlNode);
		this.idRef=getNodeAttribute(ATT_IDREF);
		this.property=getNodeAttribute(ATT_PROPERTY);
	}

	@Override
	void eval(Writer out) {
		if (idRef!=null){
			evalRef(out);
		}else if (property !=null){
			evalProperty(out);
		}
	}
	
	private void evalRef(Writer out){
		GrammarNode gnRef=getGrammar().getReference(idRef);
		if (gnRef!=null)
			gnRef.eval(out);		
	}

	private void evalProperty(Writer out){
		Object oVal=getGrammar().getProperty(property);
		try{
			out.write(String.valueOf(oVal));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
