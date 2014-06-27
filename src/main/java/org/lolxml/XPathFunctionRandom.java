package org.lolxml;

import java.util.List;
import java.util.Random;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.w3c.dom.NodeList;

public class XPathFunctionRandom implements XPathFunction {

	private static Random random=new Random();
	
	/**
	 * Random XPath wrapper. Return depends on the params, as follows:
	 * <ul>
	 * <li>random(): returns a floating-point number between 0.0 and 1.0.</li>
	 * <li>random(num): integer between 0 (inclusive) and num (exclusive).</li>
	 * <li>random(nodeset): picks a random node from the set.</li>
	 * </ul
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object evaluate(List params) throws XPathFunctionException {
		if (params==null || params.size() ==0){
			return random.nextDouble();
		}
		Object oParam = params.get(0);
		if (oParam instanceof Double){
			double d= (double) random.nextInt((int)Math.round(((Double)oParam)));
			return d;
		}
		if (oParam instanceof NodeList){
			NodeList nl = (NodeList)oParam;
			return nl.item(random.nextInt(nl.getLength()));
		}
		return null;
	}

}
