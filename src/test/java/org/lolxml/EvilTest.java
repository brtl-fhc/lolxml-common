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

import java.io.IOException;
import java.io.Writer;

import junit.framework.TestCase;

import org.lolxml.node.Grammar;

/** Malicious code */
public class EvilTest extends TestCase{

	int timeoutMs = 1000 * 10;
	
	class NullWriter extends Writer{

		long lSize = 0;
		boolean bClosed = false;
		
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			if (bClosed) throw new IOException(String.valueOf(lSize));
			lSize+=len;
		}

		@Override
		public void flush() throws IOException {
		}

		@Override
		public void close() throws IOException {
			bClosed =true;
		}
		
		public long getSize(){ return lSize; }
		
	}
	
	/** Load XML from classpath resource, and run. 
	 * 
	 * @return Evaluation result.
	 */
	private long run(String sPath) throws Exception{
		final NullWriter writer=new NullWriter();
		final Grammar root=LolXML.load(getClass().getResourceAsStream(sPath), true);
		Runnable r = new Runnable(){
			public void run() {
				root.doGenerate(writer, timeoutMs/2, 10*1024);
			};
		};
		Thread t = new Thread(r);
		t.start();
		t.join(timeoutMs);
		System.out.println(sPath + " -> chars: "+writer.getSize());
		assertFalse("Must end before timeout ("+timeoutMs+" ms)",t.isAlive());
		writer.close();
		return writer.getSize();

	}
	
    public void testSimpleRecursive() throws Exception
    {
    	run("/evil-recursive-simple.xml");
    }
    
    public void testInfiniteLoop() throws Exception
    {
    	run("/evil-infinite-loop.xml");
    }
    
    public void testInfiniteLoopSilent() throws Exception
    {
    	run("/evil-infinite-loop-silent.xml");
    }
    
    public void testRecursiveExp() throws Exception
    {
    	run("/evil-recursive-exp.xml");
    }
}
