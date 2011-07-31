package org.pelagios.graph.builder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.PelagiosGraphNode;
import org.pelagios.graph.PelagiosGraphNode.NodeType;
import org.pelagios.graph.PelagiosGraphUtils;
import org.pelagios.graph.PelagiosGraphUtils.Count;
import org.pelagios.graph.PelagiosRelationships;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.exceptions.DatasetNotFoundException;
import org.pelagios.graph.exceptions.PlaceExistsException;
import org.pelagios.graph.exceptions.PlaceNotFoundException;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

/**
 * Implementation of the PelagiosGraph interface.
 * 
 * @author Rainer Simon
 */
class PelagiosGraphImpl extends PelagiosGraph {
	
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
				subreference.createRelationshipTo(d.getBackingNode(), PelagiosRelationships.DATASET);				
			} else {
				// Sub-set - attach to parent node (if it exists)
				IndexHits<Node> parentHits = getDatasetIndex()
					.get(Dataset.KEY_NAME, parent.getName());
				
				if (parentHits.size() == 0)
					throw new DatasetNotFoundException(parent.getName());
				
				Node parentNode = parentHits.getSingle();
				d.getBackingNode().createRelationshipTo(parentNode, PelagiosRelationships.IS_SUBSET_OF);
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
	
	@Override
	public List<Dataset> findSimilarDatasets(Dataset dataset) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addGeoAnnotations(List<GeoAnnotationBuilder> records, DatasetBuilder parent)
		throws DatasetNotFoundException {
		
		Transaction tx = graphDb.beginTx();
		try {
			for (GeoAnnotationBuilder b : records) {
				IndexHits<Node> hits = getDatasetIndex().get(Dataset.KEY_NAME, parent.getName());
				if (hits.size() == 0)
					throw new DatasetNotFoundException(parent.getName());
				
				Node parentNode = hits.getSingle();
				GeoAnnotationImpl record = b.build(graphDb, getPlaceIndex());
				Node recordNode = record.getBackingNode();
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
				Node placeNode = place.getBackingNode();
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

	@Override
	public List<Place> findStronglyRelatedPlaces(Place place, int limit) {
		List<Count<Dataset>> topDatasets = 
			PelagiosGraphUtils.getTopDatasets(place.listGeoAnnotations());
	
		HashMap<Place, Count<Place>> ranks = new HashMap<Place, Count<Place>>();
		for (Count<Dataset> cd : topDatasets) {
			Dataset dataset = cd.getElement();						
			List<GeoAnnotation> annotations = dataset.listGeoAnnotations(true);
			for (Count<Place> cp : PelagiosGraphUtils.getUniquePlaces(annotations)) {
				Count<Place> rank = ranks.get(cp.getElement());
				if (rank == null) {
					rank = new Count<Place>(cp.getElement());
				}
				rank.add(cd.getCount() * cp.getCount());
				ranks.put(cp.getElement(), rank);
			}
		}
		List<Count<Place>> rankedCounts= new ArrayList<Count<Place>>(ranks.values());
		Collections.sort(rankedCounts);
		
		List<Place> relatedPlaces = new ArrayList<Place>();
		int ct = 0;
		while (ct < limit && ct < rankedCounts.size()) {
			relatedPlaces.add(rankedCounts.get(ct).getElement());
			ct++;
		}
		return relatedPlaces;
	}
	
	public List<GeoAnnotation> listReferencesTo(Place place) throws PlaceNotFoundException {
		Index<Node> index = getPlaceIndex();
		IndexHits<Node> hits = index.get(Place.KEY_URI, place.getURI());
		if (hits.size() == 0)
			throw new PlaceNotFoundException(place.getURI());
		
		Node placeNode = hits.next();
		List<GeoAnnotation> records = new ArrayList<GeoAnnotation>();
		for (Relationship r : placeNode.getRelationships(PelagiosRelationships.REFERENCES)) {
			records.add(new GeoAnnotationImpl(r.getStartNode()));
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
		List<Place> sharedPlaces = new ArrayList<Place>(
			new HashSet<Place>(datasets.get(0).listPlaces(true))
		);
		
		for (int i=1; i<datasets.size(); i++) {
			sharedPlaces = datasets.get(i).filterReferenced(sharedPlaces, true);
		}
		return sharedPlaces;
	}
	
	public Set<org.pelagios.graph.Path> findShortestPaths(Place from, Place to)
		throws PlaceNotFoundException {
		
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
		
		Set<org.pelagios.graph.Path> paths = new HashSet<org.pelagios.graph.Path>();
		for (Path p : pFinder.findAllPaths(fromNode, toNode)) {
			List<PelagiosGraphNode> nodes = new ArrayList<PelagiosGraphNode>();
			for (Node n : p.nodes()) {
				PelagiosGraphNode gNode = wrap(n);
				if (gNode.getType() != NodeType.GEOANNOTATION) {
					if (!nodes.contains(gNode))
						nodes.add(gNode);
				} else {
					DatasetImpl di = (DatasetImpl) new GeoAnnotationImpl(n).getParentDataset();
					if (!nodes.contains(di)) 
						nodes.add(di);
				}
			}

			if (nodes.size() > 2)
				paths.add(new org.pelagios.graph.Path(nodes));
		}
	
		return paths;
	}
	
	private PelagiosGraphNode wrap(Node node) {
		// TODO there must be a better way to do this
		// e.g. by adding a "type" property to 
		// every node and querying that
		try {
			node.getProperty(Place.KEY_GEOMETRY);
			return new PlaceImpl(node);
		} catch (NotFoundException e) {
			// 
		}
		
		try {
			node.getProperty(GeoAnnotation.KEY_URI);
			return new GeoAnnotationImpl(node);
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
