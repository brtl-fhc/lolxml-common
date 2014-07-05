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

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XPathFunctionEvaluate implements XPathFunction {
	
	ReferenceResolver referenceResolver;
	
	public XPathFunctionEvaluate(ReferenceResolver referenceResolver){
		this.referenceResolver=referenceResolver;
	}
	
	/**
	 * LolXML node evaluator. Resolves reference, captures eval() and returns as string.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object evaluate(List params) throws XPathFunctionException {
		String sRet="";
		if (params!=null && params.size()>0){
			String sParam=null;
			Object oParam = params.get(0);
			if (oParam instanceof NodeList){
				NodeList nl=(NodeList)oParam;
				if (nl.getLength()>0){
					Node n=nl.item(0);
					if (n!=null){
						sParam = String.valueOf(n.getTextContent());
					}
				}
			}else if (oParam instanceof Node){
				sParam = ((Node)oParam).getTextContent();
			}else{
				sParam = String.valueOf(oParam);
			}
			if (sParam!=null){
				sRet=referenceResolver.resolveReference(sParam);
			}
		}
		return sRet;
	}

}
