package org.pelagios.graph.exceptions;

public class PlaceExistsException extends Exception {

	private static final long serialVersionUID = 8578854831945860686L;

	private static final String DEFAULT_MESSAGE_TEMPLATE = 
		"The place '@label@' already exists in the graph";
	
	public PlaceExistsException(String label) {
		super(DEFAULT_MESSAGE_TEMPLATE.replace("@label@", label));
	}
	
}
