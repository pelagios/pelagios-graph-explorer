package org.pelagios.api;

import java.net.URI;


/**
 * A Place. For the time being, we consider places as something that
 * has a screen label, a URI, and a geometry.
 * 
 * @author Rainer Simon
 */
public class Place {
	
	private String label;
	
	private URI uri;
	
	private GeoJSONGeometry geojson;
	
	public Place(String label, URI uri, GeoJSONGeometry geojson) {
		this.label = label;
		this.uri = uri;
		this.geojson = geojson;
	}

	public String getLabel() {
		return label;
	}

	public URI getUri() {
		return uri;
	}

	public GeoJSONGeometry getGeoJSON() {
		return geojson;
	}

}
