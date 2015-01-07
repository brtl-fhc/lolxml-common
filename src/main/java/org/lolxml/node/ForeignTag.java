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

import java.io.Writer;

import org.lolxml.node.eval.EvaluationContext;
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
	protected void eval(EvaluationContext ctx, Writer out) {
		try{
			out.write("<"+xmlNode.getLocalName()+printAttributes()+">");
			super.eval(ctx, out);
			out.write("</"+xmlNode.getLocalName()+">");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
