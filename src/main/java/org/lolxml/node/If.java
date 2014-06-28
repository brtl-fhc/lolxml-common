package org.lolxml.node;

import java.io.Writer;

import org.w3c.dom.Node;

/** 
 * Conditional evaluator specified by inline boolean XPath (test), 
 * 	referenced boolean Exp (idref) or stored in property (property).
 */
public class If extends GrammarNode {

	String idref;
	String test;
	String property;
	
	protected If(Node xmlNode) {
		super(xmlNode);
		this.idref=getNodeAttribute(ATT_IDREF);
		this.test=getNodeAttribute(ATT_TEST);
		this.property=getNodeAttribute(ATT_PROPERTY);
		this.mixed=true;
	}
	
	@Override
	public
	void eval(Writer out) {
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
		if (bResult){
			super.eval(out);
		}
	}

}
