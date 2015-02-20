package org.lolxml.node.eval;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class OutputWriter extends FilterWriter {

	private long size = 0;
	private long capacity = 0; // 0= no limit
	
	/** Allowed size in chars */
	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}

	/** Written chars */
	public long getSize() {
		return size;
	}

	@Override
	public void write(int c) throws IOException {
		checkCapacity(1);
		super.write(c);
		size++;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		checkCapacity(len);
		super.write(cbuf, off, len);
		size += len;
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		checkCapacity(len);
		super.write(str, off, len);
		size += len;
	}

	private void checkCapacity(int iChunk) throws IOException{
		long capacity = getCapacity();
		if (capacity>0 && (getSize()+iChunk)>capacity){
			throw new IOException("Capacity exceeded: "+capacity);
		}
	}
	
	public OutputWriter(Writer out) {
		super(out);
	}
	
}
