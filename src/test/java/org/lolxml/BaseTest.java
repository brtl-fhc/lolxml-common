package org.lolxml;

import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class BaseTest extends TestCase
{
    
    public void testApp()
    {
        try{
    		Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(getClass().getResourceAsStream("/majesty.xml"));
    		GrammarNode g=Grammar.parse(doc);
    		StringWriter sw=new StringWriter();
    		g.eval(sw);
    		System.out.println(sw.toString());
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}
