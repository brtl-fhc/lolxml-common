package org.lolxml;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Encapsulate XPath evaluation */
public class XPathEvaluator {
	
	XPath xpath;
	Node ctx;
	
	void init(Node ctx){
		XPathFactory factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
		this.ctx=ctx;
	}

	public Object eval(String sExp,QName qname){
		Object oRet=null;
	    try{
	    	oRet=xpath.evaluate(sExp,ctx,qname);
	    }catch(Exception e){
	    }
	    return oRet;
	}
	
	public String evalAsString(String sExp){
		return (String) eval(sExp,XPathConstants.STRING);
	}
	
	public Boolean evalAsBoolean(String sExp){
		return (Boolean)eval(sExp,XPathConstants.BOOLEAN);
	}
	
	public NodeList evalAsNodeList(String sExp){
		return (NodeList)eval(sExp,XPathConstants.NODESET);
	}
}
