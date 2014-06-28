package org.lolxml;

import java.io.StringWriter;

import junit.framework.TestCase;

public class BaseTest extends TestCase
{

	/** Load XML from classpath resource, and run. 
	 * 
	 * @return Evaluation result.
	 */
	private String run(String sPath){
		String sRet=null;
		try{
			Main main=new Main();
			StringWriter sw=new StringWriter();
			main.loadAndWrite(getClass().getResourceAsStream(sPath), sw);
			sRet= sw.toString();
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		return sRet;
	}
	
    
    public void testExpString()
    {
    	String s=run("/exp-string-test.xml");
    	System.out.println(s);
    }
    
    public void testExpNode()
    {
    	String s=run("/exp-node-test.xml");
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
    
    public void testForeachProperty()
    {
    	String s=run("/foreach-property-test.xml");
    	System.out.println(s);
    }
    
    public void testIf()
    {
    	String s=run("/if-test.xml");
    	System.out.println(s);
    }
    
    public void testWhile()
    {
    	String s=run("/while-test.xml");
    	System.out.println(s);
    }
    
    public void testHtml()
    {
    	String s=run("/html-test.xml");
    	System.out.println(s);
    }
    
    public void testFunc()
    {
    	String s=run("/func-test.xml");
    	System.out.println(s);
    }
}
