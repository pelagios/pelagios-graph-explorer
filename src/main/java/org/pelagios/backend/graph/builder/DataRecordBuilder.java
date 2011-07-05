package org.pelagios.backend.graph.builder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

public class DataRecordBuilder {
	
	private URI dataURL;
	
	private List<URI> places = new ArrayList<URI>();
	
	public DataRecordBuilder(URI dataURL) {
		this.dataURL = dataURL;
	}
	
	public DataRecordBuilder addPlaceReference(URI uri) {
		places.add(uri);
		return this;
	}

	public DataRecordImpl build(GraphDatabaseService graphDb, Index<Node> placeIndex) {
		Node node = graphDb.createNode();
		DataRecordImpl record = new DataRecordImpl(node);
		record.setDataURL(dataURL);
		record.addPlaces(places, placeIndex);
		return record;
	}
	
}
