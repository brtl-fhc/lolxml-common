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
 * Random chooser for production rules. Evaluates a random 'case' 
 * from its children.
 */
public class Switch extends GrammarNode {
	boolean bRandom=true;
	protected Switch(Node xmlNode) {
		super(xmlNode);
	}

	@Override
	protected void eval(Writer out) throws IOException{
		GrammarNode gn=null;
		if (bRandom){
			gn=children.get(getGrammar().getRandom().nextInt(children.size()));
		}
		if (gn!=null)
			gn.eval(out);
	}

}
