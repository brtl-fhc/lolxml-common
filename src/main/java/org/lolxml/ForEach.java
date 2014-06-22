package org.lolxml;

import java.io.Writer;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ForEach extends GrammarNode {

	String idref;
	String select;
	String property;
	String var;
	
	protected ForEach(Node xmlNode) {
		super(xmlNode);
		this.idref=getNodeAttribute(ATT_IDREF);
		this.select=getNodeAttribute(ATT_SELECT);
		this.property=getNodeAttribute(ATT_PROPERTY);
		this.var=getNodeAttribute(ATT_VAR);
		this.mixed=true;
	}
	
	@Override
	void eval(Writer out) {
		NodeList items=null;
		if (select!=null){
			items=getGrammar().getXPathEvaluator().evalAsNodeList(select);
		}else if(property!=null){
			Object oVal=getGrammar().getProperty(property);
			if (oVal!=null && oVal instanceof NodeList){
				items=(NodeList)oVal;
			}
		}else if (idref!=null){
			GrammarNode gn=getGrammar().getReference(idref);
			if (gn!=null && TAG_EXP.equals(gn.xmlNode.getLocalName())){
				items = (NodeList)((Exp)gn).call(TYPE_NODESET);
			}
		}
		if (items!=null && items.getLength()>0){
			Object oldVar=getGrammar().getProperty(var);
			for (Node node : new NodeListIterator(items)) {
				getGrammar().putProperty(var, node);
				super.eval(out);
			}
			getGrammar().putProperty(var, oldVar);
		}
	}

}
