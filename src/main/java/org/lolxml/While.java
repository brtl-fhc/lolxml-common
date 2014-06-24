package org.lolxml;

import java.io.Writer;

import org.w3c.dom.Node;

public class While extends GrammarNode {

	String idref;
	String test;
	String property;
	
	protected While(Node xmlNode) {
		super(xmlNode);
		this.idref=getNodeAttribute(ATT_IDREF);
		this.test=getNodeAttribute(ATT_TEST);
		this.property=getNodeAttribute(ATT_PROPERTY);
		this.mixed=true;
	}
	
	private boolean runTest(){
		boolean bResult=false;
		if (test!=null){
			bResult=getGrammar().getXPathEvaluator().evalAsBoolean(test);
		}else if(property!=null){
			bResult = (Boolean)getGrammar().getProperty(property);
		}else if (idref!=null){
			GrammarNode gn=getGrammar().getReference(idref);
			if (gn!=null && TAG_EXP.equals(gn.xmlNode.getLocalName())){
				bResult = (Boolean)((Exp)gn).call(TYPE_BOOLEAN);
			}
		}
		return bResult;
	}
	
	@Override
	void eval(Writer out) {
		while (runTest()){
			super.eval(out);
		}
	}

}
