/* 
 * Copyright 2015 the original author or authors
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

import org.lolxml.node.eval.EvaluationContext;
import org.w3c.dom.Node;

/**
 * Conditional loop specified by inline boolean XPath (test), 
 * 	referenced boolean Exp (idref) or stored in property (property).
 */
public class While extends GrammarNode {

	String idref;
	String test;
	String property;
	
	protected While(Node xmlNode) {
		super(xmlNode);
		this.idref=getNodeAttribute(ATT_IDREF);
		this.test=getNodeAttribute(ATT_TEST);
		this.property=getNodeAttribute(ATT_PROPERTY);
		this.mixed=true;
	}
	
	private boolean runTest(EvaluationContext ctx){
		boolean bResult=false;
		if (test!=null){
			bResult=getGrammar().getXPathEvaluator(ctx).evalAsBoolean(test);
		}else if(property!=null){
			bResult = (Boolean)ctx.getProperty(property);
		}else if (idref!=null){
			GrammarNode gn=getGrammar().getReference(idref);
			if (gn!=null && TAG_EXP.equals(gn.xmlNode.getLocalName())){
				bResult = (Boolean)((Exp)gn).call(TYPE_BOOLEAN,ctx);
			}
		}
		return bResult;
	}
	
	@Override
	protected void eval(EvaluationContext ctx, Writer out) throws IOException{
		while (runTest(ctx)){
			super.eval(ctx, out);
		}
	}

}
