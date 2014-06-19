package org.lolxml;

import java.io.Writer;

import org.w3c.dom.Node;

public class Exp extends GrammarNode {

	String value;
	Object result;
	String type;
	
	protected Exp(Node xmlNode) {
		super(xmlNode);
		mixed=true;
		value=getNodeAttribute(ATT_VALUE);
		type=getNodeAttribute(ATT_TYPE);
	}
	
	@Override
	void eval(Writer out) {
		String s=getGrammar().getXPathEvaluator().evalAsString(value);
		try{
			out.write(s);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	Object call(){
		Object oRet=null;
		try{
			XPathEvaluator xEv=getGrammar().getXPathEvaluator();
			oRet=xEv.evalExpType(value, type);
		}catch(Exception e){
			e.printStackTrace();
		}
		return oRet;
	}

}
