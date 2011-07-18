package org.pelagios.graph.builder;

import java.net.URI;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.pelagios.graph.exceptions.PlaceExistsException;
import org.pelagios.graph.nodes.Place;

import com.vividsolutions.jts.geom.Geometry;

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
	private Geometry geometry;
	
	public PlaceBuilder(String label, URI uri, Geometry geometry) {
		this.label = label;
		this.uri = uri;
		this.geometry = geometry;
	}
	
	public String getLabel() {
		return label;
	}
	
	public URI getURI() {
		return uri;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	public PlaceImpl build(GraphDatabaseService graphDb, Index<Node> index)
		throws PlaceExistsException {
		
		// Check uniqueness
		IndexHits<Node> hits = index.get(Place.KEY_URI, uri);
		if (hits.size() > 0)
			throw new PlaceExistsException(label);
		
		Node node = graphDb.createNode();
		PlaceImpl place = new PlaceImpl(node);
		place.setLabel(label);
		place.setGeometry(geometry);
		place.setURI(uri);
		index.add(node, Place.KEY_URI, uri);
		index.add(node, Place.KEY_LABEL, label.toLowerCase());
		return place;
	}
	
}
