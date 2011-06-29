package org.pelagios.graph.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.pelagios.graph.DataRecord;
import org.pelagios.graph.Place;

/**
 * Implementation of the Pelagios Place graph node type.
 *  
 * @author Rainer Simon
 */
class PlaceImpl extends AbstractNodeImpl implements Place {
	
	PlaceImpl(Node backingNode) {
		super(backingNode);
	}

	public String getLabel() {
		return getAsString(Place.KEY_LABEL);
	}

	public void setLabel(String label) {
		set(Place.KEY_LABEL, label);
	}

	public double getLon() {
		return getAsDouble(Place.KEY_LON);
	}

	public void setLon(double lon) {
		set(Place.KEY_LON, Double.toString(lon));
	}

	public double getLat() {
		return getAsDouble(Place.KEY_LAT);
	}

	public void setLat(double lat) {
		set(Place.KEY_LAT, Double.toString(lat));
	}

	public URI getURI() {
		try {
			return new URI(getAsString(Place.KEY_URI));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void setURI(URI uri) {
		set(Place.KEY_URI, uri.toString());
	}

	public List<DataRecord> listDataRecords() {
		// TODO Auto-generated method stub
		return null;
	}

}
