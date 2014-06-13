package org.lolxml;

import java.io.Writer;

import org.w3c.dom.Node;

public class Text extends GrammarNode {

	protected Text(Node xmlNode) {
		super(xmlNode);
	}
	
	@Override
	void eval(Writer out) {
		try{
			out.write(xmlNode.getNodeValue());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
