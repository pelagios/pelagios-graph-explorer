package org.pelagios.graph;

import java.net.URI;

/**
 * The Pelagios Place interface.
 * 
 * @author Rainer Simon
 */
public interface Place {
	
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
	 * Sets the label for this place
	 * @param label the label
	 */
	public void setLabel(String label);

	/**
	 * Returns the longitude of this place.
	 * @return the longitude
	 */
	public double getLon();
	
	/**
	 * Sets the longitude of this place.
	 * @param lon the longitude
	 */
	public void setLon(double lon);
	
	/**
	 * Returns the latitude of this place.
	 * @return the latitude
	 */
	public double getLat();

	/**
	 * Sets the latitude of this place.
	 * @param lat the latitude
	 */
	public void setLat(double lat);
	
	/**
	 * Returns the URI of this place.
	 * @return the URI
	 */
	public URI getURI();
	
	/**
	 * Sets the URI of this place.
	 * @param uri the URI
	 */
	public void setURI(URI uri);
	
}
