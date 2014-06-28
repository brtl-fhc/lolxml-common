package org.lolxml.node;

import java.io.Writer;

import org.lolxml.node.xpath.XPathEvaluator;
import org.w3c.dom.Node;

/**
 *  Evaluates XPath expression in @value (using data element as context).
 */
public class Exp extends GrammarNode {

	String value;
	Object result;
	
	protected Exp(Node xmlNode) {
		super(xmlNode);
		mixed=true;
		value=getNodeAttribute(ATT_VALUE);
	}
	
	@Override
	public
	void eval(Writer out) {
		String s=getGrammar().getXPathEvaluator().evalAsString(value);
		try{
			out.write(s);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/** Evaluate Exp using specific return type */
	Object call(String type){
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
