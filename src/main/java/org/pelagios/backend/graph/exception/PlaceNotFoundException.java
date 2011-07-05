package org.pelagios.backend.graph.exception;

import java.net.URI;

/**
 * Thrown by the PelagiosGraph when querying for a place
 * that does not exist in the graph.
 * 
 * @author Rainer Simon
 */
public class PlaceNotFoundException extends Exception {

	private static final long serialVersionUID = -5589779150223550760L;
	
	private static final String DEFAULT_MESSAGE_TEMPLATE = 
		"The place @uri@ was not found in the graph";

	public PlaceNotFoundException(URI uri) {
		super(DEFAULT_MESSAGE_TEMPLATE.replace("@uri@", uri.toString()));
	}
	
}
