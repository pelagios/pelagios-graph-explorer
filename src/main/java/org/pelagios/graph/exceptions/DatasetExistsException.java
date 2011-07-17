package org.pelagios.graph.exceptions;

public class DatasetExistsException extends Exception {

	private static final long serialVersionUID = -3749744289835047094L;
	
	private static final String DEFAULT_MESSAGE_TEMPLATE = 
		"The dataset '@name@' already exists in the graph";
	
	public DatasetExistsException(String name) {
		super(DEFAULT_MESSAGE_TEMPLATE.replace("@name@", name));
	}
	
}
