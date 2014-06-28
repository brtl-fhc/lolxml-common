package org.lolxml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.lolxml.node.Grammar;
import org.lolxml.node.GrammarNode;
import org.w3c.dom.Document;

public class Main {

	private static final String XSD = "/lolxml.xsd";
	
	private GrammarNode root;
	
	private static Schema loadSchema(){
		try{
			return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI ).newSchema(new StreamSource(Main.class.getResourceAsStream(XSD)));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/** Load LolXML grammar */
	private static GrammarNode load(InputStream in) {
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try{
			dbf.setSchema(loadSchema());
			Document doc=dbf.newDocumentBuilder().parse(in);
			return Grammar.parse(doc);
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			try{in.close();}catch(Exception e){ e.printStackTrace(); }
		}
	}
	
	/** Set root node (for repeated runs) */
	public void setRoot(GrammarNode root) {
		this.root = root;
	}
	
	/** Parse XML grammar, set root node, and run. */
	public void loadAndWrite(InputStream xml, Writer out){
		if (root == null){
			setRoot(load(xml));
		}
		root.eval(out);
	}
	
	/** Eval root node and write result. */
	public void write(Writer out){
		root.eval(out);
	}
	
	private static void printUsage(){
		System.err.println("Usage: Main <grammar.xml>\n");
	}
	
	public static void main(String[] args){
		if (args == null || args.length==0){
			printUsage();
		}else{
			try{
				for (String arg : args){
					GrammarNode root = load(new FileInputStream(arg));
					StringWriter sw = new StringWriter();
					root.eval(sw);
					System.out.println(sw.toString());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}


}
