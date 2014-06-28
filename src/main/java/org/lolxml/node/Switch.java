package org.lolxml.node;

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
	public
	void eval(Writer out) {
		GrammarNode gn=null;
		if (bRandom){
			gn=children.get(getGrammar().getRandom().nextInt(children.size()));
		}
		if (gn!=null)
			gn.eval(out);
	}

}
