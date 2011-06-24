package org.pelagios.graph.builder;

import java.net.URI;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.pelagios.graph.Place;
import org.pelagios.graph.exception.PlaceExistsException;

public class PlaceBuilder {

	/**
	 * The label
	 */
	private String label;
	
	/**
	 * The longitude
	 */
	private double lon;
	
	/**
	 * The latitude
	 */
	private double lat;
	
	/**
	 * URI
	 */
	private URI uri;
	
	public PlaceBuilder(String label, double lon, double lat, URI uri) {
		this.label = label;
		this.lon = lon;
		this.lat = lat;
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public double getLon() {
		return lon;
	}

	public double getLat() {
		return lat;
	}

	public URI getUri() {
		return uri;
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
		place.setLon(lon);
		place.setLat(lat);
		place.setURI(uri);
		index.add(node, Place.KEY_URI, uri);
		return place;
	}
	
}
