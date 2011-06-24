package org.pelagios.graph.builder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.pelagios.graph.exception.DatasetNotFoundException;
import org.pelagios.graph.exception.PlaceExistsException;

/**
 * Implementation of the PelagiosGraph interface.
 * 
 * @author Rainer Simon
 */
class PelagiosGraphImpl implements PelagiosGraph {
	
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
	
	public void addDataset(DatasetBuilder dataset) {
		try {
			addDataset(dataset, null);
		} catch (DatasetNotFoundException e) {
			// Can never happen
			throw new RuntimeException(e);
		}
	}

	public void addDataset(DatasetBuilder dataset, DatasetBuilder parent) throws DatasetNotFoundException {
		Transaction tx = graphDb.beginTx();
		try {
			DatasetImpl d = dataset.build(graphDb, getDatasetIndex());
			if (parent == null) {
				// Top-level data set - attach to sub-reference node
				Node subreference = subreferenceNodes.get(PelagiosRelationships.DATASETS);
				subreference.createRelationshipTo(d.backingNode, PelagiosRelationships.DATASET);				
			} else {
				// Sub-set - attach to parent node (if it exists)
				IndexHits<Node> hits = getDatasetIndex()
					.get(Dataset.KEY_NAME, parent.getName());
				
				if (hits.size() == 0)
					throw new DatasetNotFoundException(parent.getName());
				
				Node parentNode = hits.getSingle();
				d.backingNode.createRelationshipTo(parentNode, PelagiosRelationships.IS_SUBSET_OF);
			}			
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	public Dataset getDataset(String name) throws DatasetNotFoundException {
		IndexHits<Node> hits = getDatasetIndex().get(Dataset.KEY_NAME, name);
		
		if (hits.size() == 0)
			throw new DatasetNotFoundException(name);
		
		return new DatasetImpl(hits.getSingle());
	}
	
	public void addPlaces(List<PlaceBuilder> places) throws PlaceExistsException {
		Transaction tx = graphDb.beginTx();
		try {
			for (PlaceBuilder b : places) {
				Node subreferenceNode = subreferenceNodes.get(PelagiosRelationships.PLACES);
				PlaceImpl place = b.build(graphDb, getPlaceIndex());
				Node placeNode = place.backingNode;
				subreferenceNode.createRelationshipTo(placeNode, PelagiosRelationships.PLACE);
			}
			tx.success();
		} finally {
			tx.finish();
		}			
	}

	public Place getPlace(URI uri) {
		IndexHits<Node> hits = getPlaceIndex().get(Place.KEY_URI, uri);
		return new PlaceImpl(hits.getSingle());
	}
	
	public Iterable<Place> listPlaces() {
		Node subreference = subreferenceNodes.get(PelagiosRelationships.PLACES);

		final Iterator<Relationship> relationships = subreference
			.getRelationships(PelagiosRelationships.PLACE).iterator();
		
		return new Iterable<Place>() {		
			public Iterator<Place> iterator() {
				return new Iterator<Place>() {
					
					public boolean hasNext() {
						return relationships.hasNext();
					}

					public Place next() {
						return new PlaceImpl(relationships.next().getEndNode());
					}

					public void remove() {
						relationships.remove();
						
					}
					
				};
			}
		};
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
