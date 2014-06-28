package org.lolxml.node;

import java.io.Writer;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/** Non-schema markup (HTML) tags. Passed through as output. */

public class ForeignTag extends GrammarNode {

	protected ForeignTag(Node xmlNode) {
		super(xmlNode);
		mixed=true;
	}

	String printAttributes(){
		NamedNodeMap attributes = xmlNode.getAttributes();
		StringBuilder sbRet=new StringBuilder();
		for (int i=0;i<attributes.getLength();i++){
			Node nAtt=attributes.item(i);
			String sName=nAtt.getLocalName();
			String sValue=nAtt.getNodeValue();
			sbRet.append(" "+sName+"=\""+sValue+"\"");
		}
		return sbRet.toString();
	}
	
	@Override
	public
	void eval(Writer out) {
		try{
			out.write("<"+xmlNode.getLocalName()+printAttributes()+">");
			super.eval(out);
			out.write("</"+xmlNode.getLocalName()+">");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
