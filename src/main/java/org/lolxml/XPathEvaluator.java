package org.lolxml;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Encapsulate XPath evaluation */
public class XPathEvaluator {
	
	XPath xpath;
	Node ctx;
	
	
	void init(Node ctx, final Map<String, Object> properties){
		XPathFactory factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
		xpath.setXPathVariableResolver(new XPathVariableResolver(){
			@Override
			public Object resolveVariable(QName variableName) {
				return properties.get(variableName.getLocalPart());
			}
		});
		xpath.setXPathFunctionResolver(new XPathFunctionResolver(){

			@Override
			public XPathFunction resolveFunction(QName name, int arity) {
				if (GrammarNode.FUNC_RANDOM.equals(name.getLocalPart())){
					return new XPathFunctionRandom();											
				}
				return null;
			}
		});
		this.ctx=ctx;
	}

	private Object eval(String sExp,QName qname){
		Object oRet=null;
	    try{
	    	oRet=xpath.evaluate(sExp,ctx,qname);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    return oRet;
	}
	
	public Object evalExpType(String exp, String sExpType){
		return eval(exp, expType(sExpType));
	}
	
	public QName expType(String sExpType){
		return	sExpType.equalsIgnoreCase(GrammarNode.TYPE_NODESET)	? XPathConstants.NODESET
				: sExpType.equalsIgnoreCase(GrammarNode.TYPE_BOOLEAN)	? XPathConstants.BOOLEAN
				: sExpType.equalsIgnoreCase(GrammarNode.TYPE_NUMBER)	? XPathConstants.NUMBER
				: sExpType.equalsIgnoreCase(GrammarNode.TYPE_NODE)		? XPathConstants.NODE 
																		: XPathConstants.STRING;
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
	
	public NodeList evalAsNode(String sExp){
		return (NodeList)eval(sExp,XPathConstants.NODE);
	}
	
	public NodeList evalAsNumber(String sExp){
		return (NodeList)eval(sExp,XPathConstants.NUMBER);
	}
	
}
