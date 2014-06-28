package org.lolxml.node;

import java.io.StringWriter;
import java.io.Writer;

import org.lolxml.node.xpath.XPathEvaluator;
import org.w3c.dom.Node;

/**
 * Store the result of either an inline XPath (if select + type are present),
 * a referenced Exp/Sym (if idref is present), or nested content, 
 * into a document property. Properties are global, and can 
 * be overwritten.
 */
public class Store extends GrammarNode {
	
	String property;
	String idRef;
	String type;
	String select;
	
	protected Store(Node xmlNode) {
		super(xmlNode);
		this.property=getNodeAttribute(ATT_PROPERTY);
		this.idRef=getNodeAttribute(ATT_IDREF);
		this.type=getNodeAttribute(ATT_TYPE);
		this.select=getNodeAttribute(ATT_SELECT);
		mixed=true;
	}
	
	/** Silent eval, stores inline XPath, IDREF result or nested content in property */
	@Override
	public
	void eval(Writer out) {
		if (select!=null){
			storeSelect();
		}else if (idRef!=null){
			storeRef();
		}else{
			storeContent();
		}
	}
	
	/** Evaluate node and keep result for reference  */
	private void storeRef(){
		String sRef=getNodeAttribute(ATT_IDREF);
		if (sRef!=null){
			GrammarNode gnRef=getGrammar().getReference(sRef);
			if (gnRef!=null){
				String sTag=gnRef.xmlNode.getLocalName();
				if (TAG_EXP.equals(sTag)){
					getGrammar().putProperty(this.property,((Exp)gnRef).call(this.type));
				}else if (TAG_SYM.equals(sTag)){
					StringWriter sw=new StringWriter();
					gnRef.eval(sw);
					getGrammar().putProperty(this.property, sw.toString());
				}
			}
		}else{
			System.err.println("Missing idref.");
		}
	}
	
	/** Evaluate and keep nested content */
	private void storeContent(){
		StringWriter sw=new StringWriter();
		super.eval(sw);
		getGrammar().putProperty(this.property, sw.toString());
	}
	
	/** Evaluate inline XPath and store result */
	private void storeSelect(){
		try{
			XPathEvaluator xEv=getGrammar().getXPathEvaluator();
			Object oRet=xEv.evalExpType(select, type==null? TYPE_STRING :type);
			getGrammar().putProperty(this.property, oRet);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
