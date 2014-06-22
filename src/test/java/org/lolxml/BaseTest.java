package org.lolxml;

import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class BaseTest extends TestCase
{
    
	private String run(String sPath){
		String sRet=null;
		try{
			DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI ).newSchema(new StreamSource(getClass().getResourceAsStream("/lolxml.xsd"))));
			Document doc=dbf.newDocumentBuilder().parse(getClass().getResourceAsStream(sPath));
			GrammarNode g=Grammar.parse(doc);
			StringWriter sw=new StringWriter();
			g.eval(sw);
			sRet= sw.toString();
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		return sRet;
	}
	
    public void testGrammars()
    {
    	System.out.println(run("/majesty.xml"));
    	System.out.println(run("/androidtxol.xml"));
    }
    
    public void testExpString()
    {
    	String s=run("/exp-string-test.xml");
    	System.out.println(s);
    }
    
    public void testExpNode()
    {
    	String s=run("/exp-string-test.xml");
    	System.out.println(s);
    }
    
    public void testStoreInline()
    {
    	String s=run("/exp-store-inline-test.xml");
    	System.out.println(s);
    }
    
    public void testForeachSelect()
    {
    	String s=run("/foreach-select-test.xml");
    	System.out.println(s);
    }
    
    public void testForeachIdref()
    {
    	String s=run("/foreach-idref-test.xml");
    	System.out.println(s);
    }
}
