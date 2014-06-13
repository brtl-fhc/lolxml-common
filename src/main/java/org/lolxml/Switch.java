package org.lolxml;

import java.io.Writer;

import org.w3c.dom.Node;

public class Switch extends GrammarNode {
	boolean bRandom=true;
	protected Switch(Node xmlNode) {
		super(xmlNode);
	}

	@Override
	void eval(Writer out) {
		GrammarNode gn=null;
		if (bRandom){
			gn=children.get(getGrammar().getRandom().nextInt(children.size()));
		}
		if (gn!=null)
			gn.eval(out);
	}

}
