package org.pelagios.graph.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.pelagios.graph.PelagiosGraphNode;
import org.pelagios.graph.importer.pleiades.locations.GeometryDeserializer;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

import com.google.gson.JsonParser;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Implementation of the Pelagios Place graph node type.
 *  
 * @author Rainer Simon
 */
class PlaceImpl extends PelagiosGraphNode implements Place {
	
	/**
	 * We'll also keep some properties in memory. Otherwise
	 * each access to them will result in a DB transaction
	 */
	private URI memCachedURI = null;
	
	private Geometry memCachedGeometry = null;
	
	PlaceImpl(Node backingNode) {
		super(backingNode);
	}

	public String getLabel() {
		return getAsString(Place.KEY_LABEL);
	}

	public void setLabel(String label) {
		set(Place.KEY_LABEL, label);
	}

	public URI getURI() {
		try {
			if (memCachedURI == null)
				memCachedURI = new URI(getAsString(Place.KEY_URI));
			
			return memCachedURI;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void setURI(URI uri) {
		memCachedURI = null;
		set(Place.KEY_URI, uri.toString());
	}
	
	public Geometry getGeometry() {
		if (memCachedGeometry == null) {
			GeometryDeserializer ds = new GeometryDeserializer();
			memCachedGeometry =
				ds.deserialize(new JsonParser()
					.parse(getAsString(KEY_GEOMETRY)), null, null);
		}
		
		return memCachedGeometry;
	}
	
	public void setGeometry(Geometry geometry) {
		set(KEY_GEOMETRY, geometry.toString());
	}

	public List<GeoAnnotation> listDataRecords() {
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

	@Override
	public NodeType getType() {
		return NodeType.PLACE;
	}
	
	Node getBackingNode() {
		return backingNode;
	}

}
