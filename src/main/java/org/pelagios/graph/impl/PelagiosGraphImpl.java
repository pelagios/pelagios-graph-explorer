package org.pelagios.graph.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.pelagios.graph.Dataset;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.PelagiosRelationships;
import org.pelagios.graph.Place;

/**
 * Implementation of the PelagiosGraph interface.
 * 
 * @author Rainer Simon
 */
public class PelagiosGraphImpl implements PelagiosGraph {
	
	/**
	 * String constants
	 */
	static final String DATASET_INDEX = "datasets";
	static final String PLACE_INDEX = "places";
	
	/**
	 * The graph DB instance
	 */
	private GraphDatabaseService graphDb;
	
	/**
	 * This graph's sub-reference nodes
	 */
	private HashMap<PelagiosRelationships, Node> subreferenceNodes;
	
	/**
	 * This graph's indexes
	 */
	private HashMap<String, Index<Node>> indexes;

	PelagiosGraphImpl(GraphDatabaseService graphDb,
			HashMap<PelagiosRelationships, Node> subreferenceNodes,
			HashMap<String, Index<Node>> indexes) {
		
		this.graphDb = graphDb;
		this.subreferenceNodes = subreferenceNodes;
		this.indexes = indexes;
	}

	public GraphDatabaseService getGraphDB() {
		return graphDb;
	}
	
	public Index<Node> getDatasetIndex() {
		return indexes.get(DATASET_INDEX);
	}
	
	public Index<Node> getPlaceIndex() {
		return indexes.get(PLACE_INDEX);
	}
	
	public List<Dataset> listTopLevelDatasets() {
		Iterable<Relationship> rels = subreferenceNodes.get(PelagiosRelationships.DATASETS)
			.getRelationships(PelagiosRelationships.DATASET);
	 
	    List<Dataset> datasets = new ArrayList<Dataset>();
	    for (Relationship rel : rels) {
	        Node datasetNode = rel.getEndNode();
	        datasets.add(new DatasetImpl(datasetNode));
	    }
	 
	    return datasets;
	}
	
	public void addDataset(Dataset dataset) {
		Transaction tx = graphDb.beginTx();
		try {
			Node subreferenceNode = subreferenceNodes.get(PelagiosRelationships.DATASETS);
			Node datasetNode = ((DatasetImpl) dataset).backingNode;
			subreferenceNode.createRelationshipTo(datasetNode, PelagiosRelationships.DATASET);
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	public Dataset getDataset(String name) {
		IndexHits<Node> hits = getDatasetIndex().get(Dataset.KEY_NAME, name);
		return new DatasetImpl(hits.getSingle());
	}
	
	public void addPlace(Place place) {
		Transaction tx = graphDb.beginTx();
		try {
			Node subreferenceNode = subreferenceNodes.get(PelagiosRelationships.PLACES);
			Node placeNode = ((PlaceImpl) place).backingNode;
			subreferenceNode.createRelationshipTo(placeNode, PelagiosRelationships.PLACE);
			tx.success();
		} finally {
			tx.finish();
		}			
	}

	public Place getPlace(URI uri) {
		IndexHits<Node> hits = getPlaceIndex().get(Place.KEY_URI, uri);
		return new PlaceImpl(hits.getSingle());
	}
	
	public void shutdown() {
		graphDb.shutdown();
	}
	
	public void clear() {
		Transaction tx = graphDb.beginTx();
		
		try {
			getDatasetIndex().delete();
			getPlaceIndex().delete();
			
			for (Node n : graphDb.getAllNodes()) {
				for (Relationship s : n.getRelationships()) {
					s.delete();
				}
				
				if (!n.equals(graphDb.getReferenceNode()))
					n.delete();
			}
			
			tx.success();
		} finally {
			tx.finish();
		}
	}

}
