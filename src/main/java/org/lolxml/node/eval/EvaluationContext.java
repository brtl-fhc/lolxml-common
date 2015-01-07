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

import java.util.HashMap;
import java.util.Map;


public class EvaluationContext {

	private Map<String,Object> evaluationProperties = new HashMap<String,Object>();

	public Map<String,Object> getEvaluationProperties(){
		return evaluationProperties;
	}
	
	public void putProperty(String sKey, Object value){
		evaluationProperties.put(sKey, value);
	}
	
	public Object getProperty(String sKey){
		return evaluationProperties.get(sKey);
	}
	
	/** Clear properties. */
	public void reset(){
		evaluationProperties.clear();
	}
}
