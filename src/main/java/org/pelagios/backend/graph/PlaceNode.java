package org.pelagios.backend.graph;

import java.net.URI;
import java.util.List;

/**
 * The Pelagios Place interface.
 * 
 * @author Rainer Simon
 */
public interface PlaceNode {
	
	/**
	 * String constants
	 */
	public static final String KEY_LABEL = "label";
	public static final String KEY_LON = "lon";
	public static final String KEY_LAT = "lat";
	public static final String KEY_URI = "uri";
	
	/**
	 * Returns the label for this place.
	 * @return the label
	 */
	public String getLabel();
	
	/**
	 * Returns the longitude of this place.
	 * @return the longitude
	 */
	public double getLon();
		
	/**
	 * Returns the latitude of this place.
	 * @return the latitude
	 */
	public double getLat();
	
	/**
	 * Returns the URI of this place.
	 * @return the URI
	 */
	public URI getURI();
	
	/**
	 * Returns a list of all data records that reference this place.
	 * @return the data records
	 */
	public List<AnnotationNode> listDataRecords();
	
}
