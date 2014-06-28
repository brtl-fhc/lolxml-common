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
	protected void eval(Writer out) {
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
