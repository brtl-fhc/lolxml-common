package org.lolxml.node.xpath;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListIterator implements Iterator<Node>, Iterable<Node>{

	NodeList nodeList;
	int i=0;
	
	public NodeListIterator(NodeList nl){
		this.nodeList=nl;
	}
	
	@Override
	public boolean hasNext() {
		return i < nodeList.getLength();
	}

	@Override
	public Node next() {
		return nodeList.item(i++);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Node> iterator() {
		return this;
	}
}