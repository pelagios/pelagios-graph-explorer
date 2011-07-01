package org.pelagios.rest.api;

/**
 * A simple class that wraps the information required for
 * drawing a link between two data set nodes in the GUI.
 * @author Rainer Simon
 *
 */
public class Link {
	
	/**
	 * The source dataset name
	 */
	String source;
	
	/**
	 * The target dataset name
	 */
	String target;
	
	/**
	 * The number of places shared among the linked data sets
	 */
	int overlap;
	
	public Link(String source, String target, int overlap) {
		this.source = source;
		this.target = target;
		this.overlap = overlap;
	}

}
