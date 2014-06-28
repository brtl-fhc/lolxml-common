package org.lolxml.node;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.lolxml.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class GrammarNode implements Constants{
	
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
		if (NAMESPACE.equals(el.getNamespaceURI())){
			return parseOwnTag(el);
		}else{
			return parseForeignTag(el);
		}
	}
	
	private static GrammarNode parseForeignTag(Element el){
		return new ForeignTag(el);
	}
	
	private static GrammarNode parseOwnTag(Element el){
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
		}else if (TAG_EXP.equals(sLocalName)){
			gn=new Exp(el);
		}else if (TAG_STORE.equals(sLocalName)){
			gn=new Store(el);
		}else if (TAG_FOREACH.equals(sLocalName)){
			gn=new ForEach(el);
		}else if (TAG_IF.equals(sLocalName)){
			gn=new If(el);
		}else if (TAG_WHILE.equals(sLocalName)){
			gn=new While(el);
		}
		if (gn!=null && el.hasAttribute("id")){
			gn.getGrammar().addReference(el.getAttribute(ATT_ID), gn);
		}
		return gn;
	}
	
	
	protected Grammar getGrammar(){
		return (Grammar)xmlNode.getOwnerDocument().getDocumentElement().getUserData(KEY_GRAMMARNODE);
	}
	
	public void eval(Writer out) {
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
	
	protected String getNodeAttribute(String s){
		String sRet=((Element)xmlNode).getAttribute(s);
		if ("".equals(sRet)){
			sRet=null;
		}
		return sRet;
	}
}
