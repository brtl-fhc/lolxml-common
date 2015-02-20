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

package org.lolxml.node.eval;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Keeps evaluation state (properties, elapsed time) */
public class EvaluationContext {

	private Map<String,Object> evaluationProperties = new HashMap<String,Object>();

	private long startMs = System.currentTimeMillis();
	
	private long timeoutMs = -1;
	
	public Map<String,Object> getEvaluationProperties(){
		return evaluationProperties;
	}
	
	public void putProperty(String sKey, Object value){
		evaluationProperties.put(sKey, value);
	}
	
	public Object getProperty(String sKey){
		return evaluationProperties.get(sKey);
	}
		
	public void onEval() throws IOException{
		checkTime(startMs, timeoutMs);
	}
	
	private void checkTime(long start, long timeoutMs) throws IOException{
		if (timeoutMs>0){
			long current = System.currentTimeMillis();
			if (current - start >= timeoutMs){
				throw new IOException("Timeout: "+timeoutMs+" ms");
			}
		}
	}
	
	public EvaluationContext(long timeoutMs){
		this.timeoutMs = timeoutMs;
	}
	
	/** Clear properties. */
	public void reset(){
		evaluationProperties.clear();
	}
}
