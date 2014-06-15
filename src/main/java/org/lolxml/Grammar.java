package org.lolxml;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
		xpe.init(this.data);
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
	
	/** Clear vars and properties. */
	private void reset(){
		documentProperties.clear();
		for (GrammarNode gn : children){
			if (gn instanceof Var){
				((Var) gn).clear();
			}
		}
	}
	
	@Override
	void eval(Writer out) {
		reset();
		// Execute last eval (should be only one)
		NodeList nl=((Element)xmlNode).getElementsByTagNameNS(NAMESPACE,TAG_EVAL);
		Element el=(Element)nl.item(nl.getLength()-1);
		Eval eval=(Eval)el.getUserData(KEY_GRAMMARNODE);
		if (eval!=null)
			eval.eval(out);
	}

	public void doGenerate(Writer w){
		eval(w);
	}
	
	public void putProperty(String sKey, Object value){
		documentProperties.put(sKey, value);
	}
	
	public Object getProperty(String sKey){
		return documentProperties.get(sKey);
	}
}
