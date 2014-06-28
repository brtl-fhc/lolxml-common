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

public class Eval extends GrammarNode {

	String idRef;
	String property;
	
	protected Eval(Node xmlNode) {
		super(xmlNode);
		this.idRef=getNodeAttribute(ATT_IDREF);
		this.property=getNodeAttribute(ATT_PROPERTY);
	}

	@Override
	protected void eval(Writer out) throws IOException{
		if (idRef!=null){
			evalRef(out);
		}else if (property !=null){
			evalProperty(out);
		}
	}
	
	private void evalRef(Writer out) throws IOException{
		GrammarNode gnRef=getGrammar().getReference(idRef);
		if (gnRef!=null)
			gnRef.eval(out);		
	}

	private void evalProperty(Writer out) throws IOException{
		Object oVal=getGrammar().getProperty(property);
		out.write(String.valueOf(oVal));
	}

}
