package org.pelagios.rest.api;

public class Occurence {
	
	String dataset;
	
	String rootdataset;
	
	int references;
	
	public Occurence(String dataset, String rootdataset, int numberOfOccurences) {
		this.dataset = dataset;
		this.rootdataset = rootdataset;
		this.references = numberOfOccurences;
	}

}
