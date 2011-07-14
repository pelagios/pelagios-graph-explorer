package org.pelagios.backend.graph.builder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipExpander;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.kernel.Traversal;
import org.pelagios.backend.graph.DataRecord;
import org.pelagios.backend.graph.Dataset;
import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.PelagiosRelationships;
import org.pelagios.backend.graph.Place;
import org.pelagios.backend.graph.exception.DatasetExistsException;
import org.pelagios.backend.graph.exception.DatasetNotFoundException;
import org.pelagios.backend.graph.exception.PlaceExistsException;
import org.pelagios.backend.graph.exception.PlaceNotFoundException;

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
	
	public void addDataset(DatasetBuilder dataset) throws DatasetExistsException {
		try {
			addDataset(dataset, null);
		} catch (DatasetNotFoundException e) {
			// Can never happen
			throw new RuntimeException(e);
		}
	}

	public void addDataset(DatasetBuilder dataset, DatasetBuilder parent) 
		throws DatasetExistsException, DatasetNotFoundException {
		
		IndexHits<Node> hits = getDatasetIndex().get(Dataset.KEY_NAME, dataset.getName());
		if (hits.size() > 0)
			throw new DatasetExistsException(dataset.getName());
		
		Transaction tx = graphDb.beginTx();
		try {
			DatasetImpl d = dataset.build(graphDb, getDatasetIndex());
			if (parent == null) {
				// Top-level data set - attach to sub-reference node
				Node subreference = subreferenceNodes.get(PelagiosRelationships.DATASETS);
				subreference.createRelationshipTo(d.backingNode, PelagiosRelationships.DATASET);				
			} else {
				// Sub-set - attach to parent node (if it exists)
				IndexHits<Node> parentHits = getDatasetIndex()
					.get(Dataset.KEY_NAME, parent.getName());
				
				if (parentHits.size() == 0)
					throw new DatasetNotFoundException(parent.getName());
				
				Node parentNode = parentHits.getSingle();
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
	
	public void addDataRecords(List<DataRecordBuilder> records, DatasetBuilder parent)
		throws DatasetNotFoundException {
		
		Transaction tx = graphDb.beginTx();
		try {
			for (DataRecordBuilder b : records) {
				IndexHits<Node> hits = getDatasetIndex().get(Dataset.KEY_NAME, parent.getName());
				if (hits.size() == 0)
					throw new DatasetNotFoundException(parent.getName());
				
				Node parentNode = hits.getSingle();
				DataRecordImpl record = b.build(graphDb, getPlaceIndex());
				Node recordNode = record.backingNode;
				parentNode.createRelationshipTo(recordNode, PelagiosRelationships.RECORD);
			}
			tx.success();
		} finally {
			tx.finish();
		}		
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
		} catch (PlaceExistsException e) {
			tx.failure();
			
			// Pass it on, man
			throw e;
		} finally {
			tx.finish();
		}			
	}

	public Place getPlace(URI uri) throws PlaceNotFoundException {		
		IndexHits<Node> hits = getPlaceIndex().get(Place.KEY_URI, uri);
		
		if (hits.size() == 0)
			throw new PlaceNotFoundException(uri);
		
		return new PlaceImpl(hits.next());
	}
	
	public List<Place> searchPlaces(String prefix, int limit) {
		List<Place> places = new ArrayList<Place>();
		if (prefix.length() > 2) {
			for (Node n : getPlaceIndex().query(Place.KEY_LABEL, prefix.toLowerCase() + "*")) {
				places.add(new PlaceImpl(n));
				if (places.size() > limit)
					break;
			}
		}
		return places;
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
	
	public List<DataRecord> listReferencesTo(Place place) throws PlaceNotFoundException {
		Index<Node> index = getPlaceIndex();
		IndexHits<Node> hits = index.get(Place.KEY_URI, place.getURI());
		if (hits.size() == 0)
			throw new PlaceNotFoundException(place.getURI());
		
		Node placeNode = hits.next();
		List<DataRecord> records = new ArrayList<DataRecord>();
		for (Relationship r : placeNode.getRelationships(PelagiosRelationships.REFERENCES)) {
			records.add(new DataRecordImpl(r.getStartNode()));
		}
		
		return records;
	}
	
	public List<Place> listSharedPlaces(List<Dataset> datasets) {
		if (datasets.size() == 0)
			return new ArrayList<Place>();
		
		if (datasets.size() == 1)
			return datasets.get(0).listPlaces(true);

		// TODO this can be further improved by sorting the
		// datasets by number of places!
		List<Place> sharedPlaces = datasets.get(0).listPlaces(true);
		for (int i=1; i<datasets.size(); i++) {
			sharedPlaces = datasets.get(i).filterReferenced(sharedPlaces, true);
		}
		return sharedPlaces;
	}
	
	public List<org.pelagios.backend.graph.Path> findShortestPath(Place from, Place to) throws PlaceNotFoundException {
		Node fromNode, toNode;
		
		Index<Node> idx = getPlaceIndex();
		IndexHits<Node> hits = idx.get(Place.KEY_URI, from.getURI());
		if (hits.size() == 0)
			throw new PlaceNotFoundException(from.getURI());
		fromNode = hits.next();
		
		hits = idx.get(Place.KEY_URI, to.getURI());
		if (hits.size() == 0)
			throw new PlaceNotFoundException(to.getURI());
		toNode = hits.next();
		
		RelationshipExpander expander = Traversal.expanderForTypes(
			PelagiosRelationships.REFERENCES, Direction.INCOMING,
			PelagiosRelationships.REFERENCES, Direction.OUTGOING,
			
			PelagiosRelationships.RECORD, Direction.INCOMING,
			PelagiosRelationships.RECORD, Direction.OUTGOING,
			
			PelagiosRelationships.IS_SUBSET_OF, Direction.INCOMING,
			PelagiosRelationships.IS_SUBSET_OF, Direction.OUTGOING
		);
		PathFinder<Path> pFinder = GraphAlgoFactory.shortestPath(expander, 8);
		
		List<org.pelagios.backend.graph.Path> paths = new ArrayList<org.pelagios.backend.graph.Path>();
		for (Path p : pFinder.findAllPaths(fromNode, toNode)) {
			List<Object> nodes = new ArrayList<Object>();
			for (Node n : p.nodes()) {
				nodes.add(wrap(n));
			}
			paths.add(new org.pelagios.backend.graph.Path(nodes));
		}
		
		return paths;
	}
	
	private Object wrap(Node node) {
		// TODO there must be a better way to do this
		// e.g. by adding a "type" property to 
		// every node and querying that
		try {
			node.getProperty(Place.KEY_LON);
			return new PlaceImpl(node);
		} catch (NotFoundException e) {
			// 
		}
		
		try {
			node.getProperty(DataRecord.KEY_URI);
			return new DataRecordImpl(node);
		} catch (NotFoundException e) {
			// 
		}
		
		try {
			node.getProperty(Dataset.KEY_NAME);
			return new DatasetImpl(node);
		} catch (NotFoundException e) {
			// 
		}
		
		return null;
	}
	
	public void shutdown() {
		graphDb.shutdown();
	}
		
}
