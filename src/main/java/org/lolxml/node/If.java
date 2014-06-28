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

import java.io.IOException;
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
	protected void eval(Writer out) throws IOException{
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
