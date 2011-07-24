package org.pelagios.graph.nodes;

import java.net.URI;
import java.util.List;

/**
 * The Pelagios Data Record interface.
 * 
 * @author Rainer Simon
 */
public interface GeoAnnotation {
	
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
	
	/**
	 * Returns the (lowest-level) data (sub-)set 
	 * this data record belongs to.
	 * @return the parent data set
	 */
	public Dataset getParentDataset(); 

	/**
	 * Returns the top-level data set this data
	 * record belongs to. If the data set is not
	 * structured into sub sets, this method
	 * will return the same data set as 
	 * getParentDataset().
	 * @return the root data set
	 */
	public Dataset getRootDataset();
	
}
