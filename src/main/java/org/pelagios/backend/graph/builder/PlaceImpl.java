package org.pelagios.backend.graph.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.pelagios.api.GeoJSONGeometry;
import org.pelagios.backend.graph.AnnotationNode;
import org.pelagios.backend.graph.PlaceNode;
import org.pelagios.pleiades.importer.locations.GeometryDeserializer;

import com.google.gson.JsonParser;

/**
 * Implementation of the Pelagios Place graph node type.
 *  
 * @author Rainer Simon
 */
class PlaceImpl extends AbstractNodeImpl implements PlaceNode {
	
	/**
	 * We'll also keep some properties in memory. Otherwise
	 * each access to them will result in a DB transaction
	 */
	private URI memCachedURI = null;
	
	private GeoJSONGeometry memCachedGeoJson = null;
	
	PlaceImpl(Node backingNode) {
		super(backingNode);
	}

	public String getLabel() {
		return getAsString(PlaceNode.KEY_LABEL);
	}

	public void setLabel(String label) {
		set(PlaceNode.KEY_LABEL, label);
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
	
	public GeoJSONGeometry getGeoJSONGeometry() {
		if (memCachedGeoJson == null) {
			GeometryDeserializer ds = new GeometryDeserializer();
			memCachedGeoJson =
				ds.deserialize(new JsonParser()
					.parse(getAsString(KEY_GEOMETRY)), null, null);
		}
		
		return memCachedGeoJson;
	}
	
	public void setGeometry(GeoJSONGeometry geometry) {
		set(KEY_GEOMETRY, geometry.toString());
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
