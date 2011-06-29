package org.pelagios.graph.exception;

/**
 * Thrown by the PelagiosGraph when (a) querying for a data set
 * that does not exist in the graph or (b) trying to add a new data 
 * sub-set with a parent (i.e. super-set) that does not exist in
 * the graph.
 * 
 * @author Rainer Simon
 */
public class DatasetNotFoundException extends Exception {

	private static final long serialVersionUID = 4568005232202481716L;

	private static final String DEFAULT_MESSAGE_TEMPLATE = 
		"The dataset '@name@' was not found in the graph";

	public DatasetNotFoundException(String name) {
		super(DEFAULT_MESSAGE_TEMPLATE.replace("@name@", name));
	}
	
}
