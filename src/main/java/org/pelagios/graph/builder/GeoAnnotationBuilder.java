package org.pelagios.graph.builder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

public class GeoAnnotationBuilder {
	
	private URI dataURL;
	
	private String label;
	
	private HashMap<String, String> properties = new HashMap<String, String>();
	
	private List<URI> places = new ArrayList<URI>();
	
	public GeoAnnotationBuilder() { }
	
	public GeoAnnotationBuilder(URI dataURL) {
		this.dataURL = dataURL;
	}
	
	public void setDataURL(URI dataURL) {
		this.dataURL = dataURL;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public GeoAnnotationBuilder addPlaceReference(URI uri) {
		places.add(uri);
		return this;
	}
	
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}

	public GeoAnnotationImpl build(GraphDatabaseService graphDb, Index<Node> placeIndex) {
		Node node = graphDb.createNode();
		GeoAnnotationImpl record = new GeoAnnotationImpl(node);
		record.setDataURL(dataURL);
		record.setLabel(label);
		record.addPlaces(places, placeIndex);
		
		for (String key : properties.keySet()) {
			record.addProperty(key, properties.get(key));
		}
		
		return record;
	}
	
}
