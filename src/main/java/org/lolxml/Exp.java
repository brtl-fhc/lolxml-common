package org.lolxml;

import java.io.Writer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Exp extends GrammarNode {

	String value;
	Object result;
	
	protected Exp(Node xmlNode) {
		super(xmlNode);
		mixed=true;
		autoEval=false;
		value= ((Element)xmlNode).getAttribute(ATT_VALUE);
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
			oRet= getGrammar().getXPathEvaluator().evalAsNodeList(value);
		}catch(Exception e){
			e.printStackTrace();
		}
		return oRet;
	}

}
