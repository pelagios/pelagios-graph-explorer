package org.pelagios.graph;

import java.net.URI;

public interface PlaceFactory {

	public Place createPlace(String label, double lon, double lat, URI uri);
	
}
