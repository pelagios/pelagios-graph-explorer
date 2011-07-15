package org.pelagios.backend.graph;

import java.net.URI;
import java.util.List;

import org.pelagios.api.GeoJSONGeometry;

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
	public static final String KEY_URI = "uri";
	public static final String KEY_GEOMETRY = "geometry";
	
	/**
	 * Returns the label for this place.
	 * @return the label
	 */
	public String getLabel();
	
	/**
	 * Returns the URI of this place.
	 * @return the URI
	 */
	public URI getURI();
	
	/**
	 * Returns the geometry of this place
	 * @return
	 */
	public GeoJSONGeometry getGeoJSONGeometry();	
	
	/**
	 * Returns a list of all data records that reference this place.
	 * @return the data records
	 */
	public List<AnnotationNode> listDataRecords();
	
}
