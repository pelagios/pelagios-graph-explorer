package org.pelagios.rest.api;

public class CoReference {
	
	String dataset;
	
	int src;
	
	int dest;
	
	public CoReference(String dataset, int src, int dest) {
		this.dataset = dataset;
		this.src = src;
		this.dest = dest;
	}

}
