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
		autoEval=false;
		value=getNodeAttribute(ATT_VALUE);
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
			oRet= value.equalsIgnoreCase(TYPE_NODESET)	? xEv.evalAsNodeList(value)
				: value.equalsIgnoreCase(TYPE_BOOLEAN)	? xEv.evalAsBoolean(value)
				: value.equalsIgnoreCase(TYPE_NUMBER)	? xEv.evalAsNumber(value)
				: value.equalsIgnoreCase(TYPE_NODE)		? xEv.evalAsNode(value) 
														: xEv.evalAsString(value);
		}catch(Exception e){
			e.printStackTrace();
		}
		return oRet;
	}

}
