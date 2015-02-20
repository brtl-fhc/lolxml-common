/* 
 * Copyright 2015 the original author or authors
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

import java.io.StringWriter;

import junit.framework.TestCase;

import org.lolxml.node.Grammar;
import org.lolxml.node.eval.OutputWriter;

public class BaseTest extends TestCase
{

	/** Load XML from classpath resource, and run. 
	 * 
	 * @return Evaluation result.
	 */
	private String run(String sPath){
		String sRet=null;
		try{
			StringWriter sw = new StringWriter();
			OutputWriter out=new OutputWriter(sw);
			Grammar root=LolXML.load(getClass().getResourceAsStream(sPath), true);
			root.doGenerate(out);
			sRet= sw.toString();
			assertEquals(sRet.length(), out.getSize());
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
    
    public void testWhitespace()
    {
    	String s=run("/whitespace-test.xml");
    	System.out.println(s);
    }
    
    public void testFuncEvaluate()
    {
    	String s=run("/func-evaluate-test.xml");
    	System.out.println(s);
    }
    
    private class ThreadedRunner implements Runnable{
    	Grammar g;
    	boolean bResult = true;
    	
    	public ThreadedRunner(Grammar g) { this.g = g; }
    	
    	@Override
    	public void run() {
    		for (int i=0; i<100;i++){
	    		StringWriter sw=new StringWriter();
	    		g.doGenerate(sw);
	    		if (!validThreadedResult(sw.toString())){
	    			bResult=false;
	    			break;
	    		}
    		}
    	}
    	
        private boolean validThreadedResult(String s){
        	boolean bRet= "1111111111111111111111111".equals(s) 
        			|| "2222222222222222222222222".equals(s) 
        			|| "3333333333333333333333333".equals(s);
        	if (!bRet){
        		System.err.println(s);
        	}
        	return bRet;
        }
    }
    
    
    public void testThreadsafe() throws Exception{
		Grammar root=LolXML.load(getClass().getResourceAsStream("/threadsafe-test.xml"), true);
		
		ThreadedRunner[] runners=new ThreadedRunner[20];
		Thread[] threads=new Thread[20];

		for (int i=0;i<runners.length; i++){
			runners[i]=new ThreadedRunner(root);
			threads[i]=new Thread(runners[i]);
			threads[i].start();
		}
		for (int i=0;i<runners.length; i++){
			threads[i].join();
			assertTrue(runners[i].bResult);
		}
    }


}
