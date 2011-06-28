package org.pelagios.graph;

import java.net.URI;
import java.util.List;

/**
 * The Pelagios Data Record interface.
 * 
 * @author Rainer Simon
 */
public interface DataRecord {
	
	/**
	 * String constants
	 */
	public static final String KEY_URI = "uri";
	
	/**
	 * Returns the HTTP URL to the actual data record online.
	 * @return the record's HTTP URL
	 */
	public URI getDataURL();
	
	/**
	 * Lists all places referred to in this data record 
	 * @return the places
	 */
	public List<Place> listPlaces();

}
