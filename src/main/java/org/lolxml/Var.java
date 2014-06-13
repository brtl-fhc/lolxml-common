package org.lolxml;

import java.io.StringWriter;
import java.io.Writer;

import org.w3c.dom.Node;

public class Var extends GrammarNode {

	String sValue;
	
	protected Var(Node xmlNode) {
		super(xmlNode);
	}
	
	public void clear(){
		sValue=null;
	}

	@Override
	void eval(Writer out) {
		if (sValue==null){
			StringWriter sw=new StringWriter();
			super.eval(sw);
			sValue=sw.toString();
		}
		try{
			out.write(sValue);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
