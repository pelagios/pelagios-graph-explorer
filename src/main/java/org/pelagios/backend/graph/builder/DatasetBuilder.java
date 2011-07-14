package org.pelagios.backend.graph.builder;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.pelagios.backend.graph.DatasetNode;

public class DatasetBuilder {

	private String name;
	
	public DatasetBuilder(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public DatasetImpl build(GraphDatabaseService graphDb, Index<Node> index) {
		Node node = graphDb.createNode();
		DatasetImpl dataset = new DatasetImpl(node);
		dataset.setName(name);
		index.add(node, DatasetNode.KEY_NAME, name);
		return dataset;
	}

}
