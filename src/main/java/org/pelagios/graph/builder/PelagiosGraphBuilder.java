package org.pelagios.graph.builder;

import java.util.HashMap;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.PelagiosRelationships;

/**
 * Implementation of the PelagiosGraph factory.
 * 
 * @author Rainer Simon
 */
public class PelagiosGraphBuilder {
	
	private String directory;
	
	public PelagiosGraphBuilder(String directory) {
		this.directory = directory;
	}
	
	public PelagiosGraph build() {
		GraphDatabaseService graphDb = new EmbeddedGraphDatabase(directory);
		Transaction tx = graphDb.beginTx();
		try {
			// Initialize sub-reference nodes
			Node datasetSubreference = null;
			Node placeSubreference = null;
			for (Relationship r : graphDb.getReferenceNode().getRelationships()) {
				if (r.isType(PelagiosRelationships.DATASETS)) {
					datasetSubreference = r.getEndNode();
				} else if (r.isType(PelagiosRelationships.PLACES)) {
					placeSubreference = r.getEndNode();
				}
			}
			
			if (datasetSubreference == null)
				datasetSubreference = createDatasetSubreferenceNode(graphDb);
			
			if (placeSubreference == null)
				placeSubreference = createPlaceSubreferenceNode(graphDb);
			
			HashMap<PelagiosRelationships, Node> subreferences = new HashMap<PelagiosRelationships, Node>();
			subreferences.put(PelagiosRelationships.DATASETS, datasetSubreference);
			subreferences.put(PelagiosRelationships.PLACES, placeSubreference);
			
			// Initialize indexes
			IndexManager im = graphDb.index();
			HashMap<String, Index<Node>> indexes = new HashMap<String, Index<Node>>();
			indexes.put(PelagiosGraphImpl.DATASET_INDEX, im.forNodes(PelagiosGraphImpl.DATASET_INDEX));
			indexes.put(PelagiosGraphImpl.PLACE_INDEX, im.forNodes(PelagiosGraphImpl.PLACE_INDEX));
			
			tx.success();
			return new PelagiosGraphImpl(graphDb, subreferences, indexes);
		} finally {
			tx.finish();
		}
	}
	
	private Node createDatasetSubreferenceNode(GraphDatabaseService graphDb) {
		Node node = graphDb.createNode();
		graphDb.getReferenceNode().createRelationshipTo(node, PelagiosRelationships.DATASETS);
		return node;
	}
	
	private Node createPlaceSubreferenceNode(GraphDatabaseService graphDb) {
		Node node = graphDb.createNode();
		graphDb.getReferenceNode().createRelationshipTo(node, PelagiosRelationships.PLACES);
		return node;
	}

}
