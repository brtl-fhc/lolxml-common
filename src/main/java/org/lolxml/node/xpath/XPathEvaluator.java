/* 
 * Copyright 2014 the original author or authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.lolxml.node.xpath;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import org.lolxml.Constants;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Encapsulate XPath evaluation */
public class XPathEvaluator implements Constants{
	
	XPath xpath;
	Node ctx;
	
	public void init(Node ctx, final Map<String, Object> properties, 
			final ReferenceResolver referenceResolver){
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
				if (FUNC_RANDOM.equals(name.getLocalPart())){
					return new XPathFunctionRandom();											
				}
				if (FUNC_EVALUATE.equals(name.getLocalPart())){
					return new XPathFunctionEvaluate(referenceResolver);
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
		return	sExpType.equalsIgnoreCase(TYPE_NODESET)		? XPathConstants.NODESET
				: sExpType.equalsIgnoreCase(TYPE_BOOLEAN)	? XPathConstants.BOOLEAN
				: sExpType.equalsIgnoreCase(TYPE_NUMBER)	? XPathConstants.NUMBER
				: sExpType.equalsIgnoreCase(TYPE_NODE)		? XPathConstants.NODE 
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
