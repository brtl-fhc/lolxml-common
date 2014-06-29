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

package org.lolxml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.lolxml.node.Grammar;
import org.w3c.dom.Document;

/** Main class */
public class LolXML {

	private static final String XSD = "/lolxml.xsd";
		
	private static Schema loadSchema(){
		try{
			return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI ).newSchema(new StreamSource(LolXML.class.getResourceAsStream(XSD)));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/** Load LolXML grammar
	 * @param bValidate Use Schema to validate (might crash on Android). */
	public static Grammar load(InputStream in, boolean bValidate) {
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try{
			if (bValidate){
				dbf.setSchema(loadSchema());
			}
			Document doc=dbf.newDocumentBuilder().parse(in);
			return (Grammar)Grammar.parse(doc);
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			try{in.close();}catch(Exception e){ e.printStackTrace(); }
		}
	}
		
	private static void printUsage(){
		System.err.println("Usage: LolXML <grammar.xml>\n");
	}
	
	public static void main(String[] args){
		if (args == null || args.length==0){
			printUsage();
		}else{
			try{
				for (String arg : args){
					Grammar root = load(new FileInputStream(arg),true);
					StringWriter sw = new StringWriter();
					root.doGenerate(sw);
					System.out.println(sw.toString());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}


}
