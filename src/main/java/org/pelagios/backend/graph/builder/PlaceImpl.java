package org.pelagios.backend.graph.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.pelagios.backend.graph.AnnotationNode;
import org.pelagios.backend.graph.PlaceNode;

/**
 * Implementation of the Pelagios Place graph node type.
 *  
 * @author Rainer Simon
 */
class PlaceImpl extends AbstractNodeImpl implements PlaceNode {
	
	/**
	 * We'll also keep the URI in memory. Otherwise
	 * each .equals will result in a DB transaction
	 */
	private URI memCachedURI = null;
	
	PlaceImpl(Node backingNode) {
		super(backingNode);
	}

	public String getLabel() {
		return getAsString(PlaceNode.KEY_LABEL);
	}

	public void setLabel(String label) {
		set(PlaceNode.KEY_LABEL, label);
	}

	public double getLon() {
		return getAsDouble(PlaceNode.KEY_LON);
	}

	public void setLon(double lon) {
		set(PlaceNode.KEY_LON, Double.toString(lon));
	}

	public double getLat() {
		return getAsDouble(PlaceNode.KEY_LAT);
	}

	public void setLat(double lat) {
		set(PlaceNode.KEY_LAT, Double.toString(lat));
	}

	public URI getURI() {
		try {
			if (memCachedURI == null)
				memCachedURI = new URI(getAsString(PlaceNode.KEY_URI));
			
			return memCachedURI;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void setURI(URI uri) {
		memCachedURI = null;
		set(PlaceNode.KEY_URI, uri.toString());
	}

	public List<AnnotationNode> listDataRecords() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Places are considered equal as soon as 
	 * their URIs are equal! We don't care about
	 * the rest.
	 */
	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof PlaceImpl))
			return false;
		
		PlaceImpl other = (PlaceImpl) arg;
		return getURI().equals(other.getURI());
	}
	
	@Override
	public int hashCode() {
		return getURI().hashCode();
	}
	
	@Override
	public String toString() {
		return "PLACE: " + getLabel() + " - " + getURI();
	}

}
