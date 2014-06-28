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

import org.lolxml.node.xpath.NodeListIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Iterate through nodeset specified by inline XPath (select), referenced
 * Exp (idref) or stored in property (property), and evaluate content.
 * Exposes active node as property (name declared in "var" attribute).
 */
public class ForEach extends GrammarNode {

	String idref;
	String select;
	String property;
	String var;
	
	protected ForEach(Node xmlNode) {
		super(xmlNode);
		this.idref=getNodeAttribute(ATT_IDREF);
		this.select=getNodeAttribute(ATT_SELECT);
		this.property=getNodeAttribute(ATT_PROPERTY);
		this.var=getNodeAttribute(ATT_VAR);
		this.mixed=true;
	}
	
	@Override
	protected void eval(Writer out) throws IOException{
		NodeList items=null;
		if (select!=null){
			items=getGrammar().getXPathEvaluator().evalAsNodeList(select);
		}else if(property!=null){
			Object oVal=getGrammar().getProperty(property);
			if (oVal!=null && oVal instanceof NodeList){
				items=(NodeList)oVal;
			}
		}else if (idref!=null){
			GrammarNode gn=getGrammar().getReference(idref);
			if (gn!=null && TAG_EXP.equals(gn.xmlNode.getLocalName())){
				items = (NodeList)((Exp)gn).call(TYPE_NODESET);
			}
		}
		if (items!=null && items.getLength()>0){
			Object oldVar=getGrammar().getProperty(var);
			for (Node node : new NodeListIterator(items)) {
				getGrammar().putProperty(var, node);
				super.eval(out);
			}
			getGrammar().putProperty(var, oldVar);
		}
	}

}
