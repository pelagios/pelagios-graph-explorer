package org.pelagios.graph.builder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

public class GeoAnnotationBuilder {
	
	private URI dataURL;
	
	private List<URI> places = new ArrayList<URI>();
	
	public GeoAnnotationBuilder() { }
	
	public GeoAnnotationBuilder(URI dataURL) {
		this.dataURL = dataURL;
	}
	
	public void setDataURL(URI dataURL) {
		this.dataURL = dataURL;
	}
	
	public GeoAnnotationBuilder addPlaceReference(URI uri) {
		places.add(uri);
		return this;
	}

	public GeoAnnotationImpl build(GraphDatabaseService graphDb, Index<Node> placeIndex) {
		Node node = graphDb.createNode();
		GeoAnnotationImpl record = new GeoAnnotationImpl(node);
		record.setDataURL(dataURL);
		record.addPlaces(places, placeIndex);
		return record;
	}
	
}
