package org.lolxml;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class GrammarNode {

	public static final String TAG_DATA="data";
	public static final String TAG_SYM="sym";
	public static final String TAG_SWITCH="switch";
	public static final String TAG_CASE="case";
	public static final String TAG_EVAL="eval";
	public static final String TAG_VAR="var";
	public static final String TAG_GRAMMAR="grammar";
	public static final String TAG_EXP="exp";

	public static final String ATT_ID="id";
	public static final String ATT_TEST="test";
	public static final String ATT_IDREF="idref";
	public static final String ATT_VALUE="value";

	public static final String KEY_GRAMMARNODE="grammarNode";
	
	public static final String NAMESPACE = "http://lolxml.org";
	
	protected boolean mixed;
	protected Node xmlNode;
	protected List<GrammarNode> children;
	protected boolean autoEval=true;
	
	protected GrammarNode(Node xmlNode){
		this.xmlNode=xmlNode;
		xmlNode.setUserData(KEY_GRAMMARNODE, this, null);
		children=new ArrayList<GrammarNode>();
		mixed=false;
	}
	
	public static GrammarNode parse(Node node){
		GrammarNode gnRet=null;
		switch (node.getNodeType()){
		case Node.DOCUMENT_NODE:
			gnRet=parseElement(((Document)node).getDocumentElement());
			break;
		case Node.CDATA_SECTION_NODE:
		case Node.TEXT_NODE:
			gnRet=parseText(node);
			break;			
		case Node.ELEMENT_NODE:
			gnRet=parseElement((Element)node);
			break;
		}
		if (gnRet!=null){
			NodeList nl=node.getChildNodes();
			for (int i=0;i<nl.getLength();i++){
				GrammarNode gnChild=parse(nl.item(i));
				if (gnChild==null || (gnChild instanceof Text  && !gnRet.mixed)){
					// Ignore text node in non-mixed elems.
				}else{
					gnRet.children.add(gnChild);
				}
			}
		}
		return gnRet;
	}
	
	private static GrammarNode parseText(Node node){
		return new Text(node);
	}
	
	private static GrammarNode parseElement(Element el){
		GrammarNode gn=null;
		String sLocalName = el.getLocalName();
		if (TAG_CASE.equals(sLocalName)){
			gn=new Case(el);
		}else if (TAG_DATA.equals(sLocalName)){
			gn=new Data(el);
		}else if (TAG_EVAL.equals(sLocalName)){
			gn=new Eval(el);
		}else if (TAG_GRAMMAR.equals(sLocalName)){
			gn=new Grammar(el);
		}else if (TAG_SWITCH.equals(sLocalName)){
			gn=new Switch(el);
		}else if (TAG_SYM.equals(sLocalName)){
			gn=new Sym(el);
		}else if (TAG_VAR.equals(sLocalName)){
			gn=new Var(el);
		}else if (TAG_EXP.equals(sLocalName)){
			gn=new Exp(el);
		}
		if (gn!=null && el.hasAttribute("id")){
			gn.getGrammar().addReference(el.getAttribute(ATT_ID), gn);
		}
		return gn;
	}
	
	
	protected Grammar getGrammar(){
		return (Grammar)xmlNode.getOwnerDocument().getDocumentElement().getUserData(KEY_GRAMMARNODE);
	}
	
	void eval(Writer out) {
		for (GrammarNode gn : children){
			if (gn.isAutoEval()){
				gn.eval(out);
			}
		}
	}

	public boolean isAutoEval() {
		return autoEval;
	}

	public void setAutoEval(boolean autoEval) {
		this.autoEval = autoEval;
	}
}
