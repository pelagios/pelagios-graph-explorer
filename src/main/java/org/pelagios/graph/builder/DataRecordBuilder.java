package org.pelagios.graph.builder;

import java.net.URI;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class DataRecordBuilder {
	
	private URI dataURL;
	
	public DataRecordBuilder(URI dataURL) {
		this.dataURL = dataURL;
	}

	public DataRecordImpl build(GraphDatabaseService graphDb) {
		Node node = graphDb.createNode();
		DataRecordImpl record = new DataRecordImpl(node);
		record.setDataURL(dataURL);
		return record;
	}
	
}
