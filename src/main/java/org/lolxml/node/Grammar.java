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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.lolxml.node.xpath.XPathEvaluator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Grammar extends GrammarNode{
	
	Random random;
	Element data;
	Map<String,GrammarNode> references;
	XPathEvaluator xpe;
	Map<String,Object> documentProperties;
	
	protected Grammar(Node xmlNode){
		super(xmlNode);
		this.random=new Random();
		this.references=new HashMap<String, GrammarNode>();
		this.documentProperties=new HashMap<String,Object>();
	};
	
	void setData(Element dataElement){
		this.data=dataElement;
		xpe=new XPathEvaluator();
		xpe.init(this.data,documentProperties);
	}
	
	public void addReference(String id,GrammarNode node){
		references.put(id, node);
	}
	
	public GrammarNode getReference(String id){
		return references.get(id);
	}
	
	public Random getRandom(){
		return random;
	}
	
	public XPathEvaluator getXPathEvaluator(){
		return xpe;
	}
	
	/** Clear properties. */
	private void reset(){
		documentProperties.clear();
	}
	
	@Override
	protected void eval(Writer out) throws IOException{
		reset();
		// Execute last eval (should be only one)
		NodeList nl=((Element)xmlNode).getElementsByTagNameNS(NAMESPACE,TAG_EVAL);
		Element el=(Element)nl.item(nl.getLength()-1);
		Eval eval=(Eval)el.getUserData(KEY_GRAMMARNODE);
		if (eval!=null)
			eval.eval(out);
	}

	public void doGenerate(Writer w){
		try{
			eval(w);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	void putProperty(String sKey, Object value){
		documentProperties.put(sKey, value);
	}
	
	Object getProperty(String sKey){
		return documentProperties.get(sKey);
	}
}
