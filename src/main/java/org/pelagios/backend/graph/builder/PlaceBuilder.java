package org.pelagios.backend.graph.builder;

import java.net.URI;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.pelagios.api.GeoJSONGeometry;
import org.pelagios.backend.graph.PlaceNode;
import org.pelagios.backend.graph.exception.PlaceExistsException;

public class PlaceBuilder {

	/**
	 * The label
	 */
	private String label;
	
	/**
	 * URI
	 */
	private URI uri;
	
	/**
	 * Geometry
	 */
	private GeoJSONGeometry geometry;
	
	public PlaceBuilder(String label, URI uri, GeoJSONGeometry geometry) {
		this.label = label;
		this.uri = uri;
		this.geometry = geometry;
	}
	
	public PlaceImpl build(GraphDatabaseService graphDb, Index<Node> index)
		throws PlaceExistsException {
		
		// Check uniqueness
		IndexHits<Node> hits = index.get(PlaceNode.KEY_URI, uri);
		if (hits.size() > 0)
			throw new PlaceExistsException(label);
		
		Node node = graphDb.createNode();
		PlaceImpl place = new PlaceImpl(node);
		place.setLabel(label);
		place.setGeometry(geometry);
		place.setURI(uri);
		index.add(node, PlaceNode.KEY_URI, uri);
		index.add(node, PlaceNode.KEY_LABEL, label.toLowerCase());
		return place;
	}
	
}
