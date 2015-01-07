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
import java.io.StringWriter;
import java.io.Writer;

import org.lolxml.node.eval.EvaluationContext;
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
	protected void eval(EvaluationContext ctx, Writer out) throws IOException{
		if (select!=null){
			storeSelect(ctx);
		}else if (idRef!=null){
			storeRef(ctx);
		}else{
			storeContent(ctx);
		}
	}
	
	/** Evaluate node and keep result for reference  */
	private void storeRef(EvaluationContext ctx) throws IOException{
		String sRef=getNodeAttribute(ATT_IDREF);
		if (sRef!=null){
			GrammarNode gnRef=getGrammar().getReference(sRef);
			if (gnRef!=null){
				String sTag=gnRef.xmlNode.getLocalName();
				if (TAG_EXP.equals(sTag)){
					ctx.putProperty(this.property,((Exp)gnRef).call(this.type,ctx));
				}else if (TAG_SYM.equals(sTag)){
					StringWriter sw=new StringWriter();
					gnRef.eval(ctx, sw);
					ctx.putProperty(this.property, sw.toString());
				}
			}
		}else{
			System.err.println("Missing idref.");
		}
	}
	
	/** Evaluate and keep nested content */
	private void storeContent(EvaluationContext ctx) throws IOException{
		StringWriter sw=new StringWriter();
		super.eval(ctx, sw);
		ctx.putProperty(this.property, sw.toString());
	}
	
	/** Evaluate inline XPath and store result */
	private void storeSelect(EvaluationContext ctx){
		try{
			XPathEvaluator xEv=getGrammar().getXPathEvaluator(ctx);
			Object oRet=xEv.evalExpType(select, type==null? TYPE_STRING :type);
			ctx.putProperty(this.property, oRet);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
