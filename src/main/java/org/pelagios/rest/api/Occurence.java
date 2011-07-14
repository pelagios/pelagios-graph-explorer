package org.pelagios.rest.api;

public class Occurence {
	
	String dataset;
	
	int references;
	
	public Occurence(String dataset, int numberOfOccurences) {
		this.dataset = dataset;
		this.references = numberOfOccurences;
	}

}
