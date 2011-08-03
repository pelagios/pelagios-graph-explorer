package org.pelagios.graph.builder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
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
 * The core class of the Pelagios Graph Explorer! The implementation of the
 * PelagiosGraph abstract class.
 *  
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
class PelagiosGraphImpl extends PelagiosGraph {

    /**
     * String constants
     */
    static final String DATASET_INDEX = "datasets";
    static final String PLACE_INDEX = "places";

    /**
     * The Neo4j instance
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
    
    /**
     * A logger
     */
    private Logger log = Logger.getLogger(PelagiosGraphImpl.class);
    
    /**
     * The graph traversal strategy for searching shortest paths
     */
    private RelationshipExpander expander = Traversal.expanderForTypes(
            PelagiosRelationships.REFERENCES, Direction.INCOMING,
            PelagiosRelationships.REFERENCES, Direction.OUTGOING,
            
            PelagiosRelationships.GEOANNOTATION, Direction.INCOMING,
            PelagiosRelationships.GEOANNOTATION, Direction.OUTGOING,
            
            PelagiosRelationships.IS_SUBSET_OF, Direction.INCOMING,
            PelagiosRelationships.IS_SUBSET_OF, Direction.OUTGOING);


    PelagiosGraphImpl(GraphDatabaseService graphDb, HashMap<PelagiosRelationships, Node> subreferenceNodes,
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

    @Override
    public List<Dataset> listTopLevelDatasets() {
        Iterable<Relationship> rels = subreferenceNodes.get(PelagiosRelationships.DATASETS).getRelationships(
                PelagiosRelationships.DATASET);

        List<Dataset> datasets = new ArrayList<Dataset>();
        for (Relationship rel : rels) {
            Node datasetNode = rel.getEndNode();
            datasets.add(new DatasetImpl(datasetNode));
        }

        return datasets;
    }

    @Override
    public void addDataset(DatasetBuilder dataset) throws DatasetExistsException {
        try {
            addDataset(dataset, null);
        } catch (DatasetNotFoundException e) {
            // Can never happen
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addDataset(DatasetBuilder dataset, DatasetBuilder parent) throws DatasetExistsException,
            DatasetNotFoundException {

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
                IndexHits<Node> parentHits = getDatasetIndex().get(Dataset.KEY_NAME, parent.getName());

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

    @Override
    public Dataset getDataset(String name) throws DatasetNotFoundException {
        IndexHits<Node> hits = getDatasetIndex().get(Dataset.KEY_NAME, name);

        if (hits.size() == 0)
            throw new DatasetNotFoundException(name);

        return new DatasetImpl(hits.getSingle());
    }

    @Override
    public void addGeoAnnotations(List<GeoAnnotationBuilder> annotations, DatasetBuilder parent)
            throws DatasetNotFoundException {

        Transaction tx = graphDb.beginTx();
        try {
            for (GeoAnnotationBuilder b : annotations) {
                IndexHits<Node> hits = getDatasetIndex().get(Dataset.KEY_NAME, parent.getName());
                if (hits.size() == 0)
                    throw new DatasetNotFoundException(parent.getName());

                try {
                    Node parentNode = hits.getSingle();
                    GeoAnnotationImpl annotation = b.build(graphDb, getPlaceIndex());
                    Node annotationNode = annotation.getBackingNode();
                    parentNode.createRelationshipTo(annotationNode, PelagiosRelationships.GEOANNOTATION);
                } catch (PlaceNotFoundException e) {
                    log.warn("Place " + b.getBodyURI() + " not in graph - skipping this annotation");
                }
            }
            tx.success();
        } finally {
            tx.finish();
        }
    }

    @Override
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

            // Pass it on, man!
            throw e;
        } finally {
            tx.finish();
        }
    }

    @Override
    public Place getPlace(URI uri) throws PlaceNotFoundException {
        IndexHits<Node> hits = getPlaceIndex().get(Place.KEY_URI, uri);

        if (hits.size() == 0)
            throw new PlaceNotFoundException(uri);

        return new PlaceImpl(hits.next());
    }

    @Override
    public List<Place> searchPlaces(String prefix, int limit) {
        List<Place> places = new ArrayList<Place>();
        
        // Note we're enforcing a minimum prefix length of two 
        if (prefix.length() > 2) {
            for (Node n : getPlaceIndex().query(Place.KEY_LABEL, prefix.toLowerCase() + "*")) {
                places.add(new PlaceImpl(n));
                if (places.size() > limit)
                    break;
            }
        }
        return places;
    }

    @Override
    public Iterable<Place> listPlaces() {
        Node subreference = subreferenceNodes.get(PelagiosRelationships.PLACES);

        final Iterator<Relationship> relationships = subreference.getRelationships(PelagiosRelationships.PLACE)
                .iterator();

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

    /**
     * TODO the way ranking works right now is a bit arbitrary. We might
     * play with different ways of ranking 'strong relations' in the future.
     */
    @Override
    public List<Place> findStronglyRelatedPlaces(Place place, int limit) {
        // Get all annotations that reference this place in all data sets
        List<GeoAnnotation> referencesToPlace = place.listGeoAnnotations();
        
        // Create a list of those data sets, sorted by how often they reference the place
        List<Count<Dataset>> topDatasets = PelagiosGraphUtils.rankDatasets(referencesToPlace);

        // Now loop through the datasets
        HashMap<Place, Count<Place>> ranks = new HashMap<Place, Count<Place>>();
        for (Count<Dataset> d : topDatasets) {
            // For each, get ALL annotations...
            Dataset dataset = d.getElement();
            List<GeoAnnotation> annotations = dataset.listGeoAnnotations(true);
            for (Count<Place> p : PelagiosGraphUtils.getUniquePlaces(annotations)) {
                // ...and create a rank R(place, p) = (number of references to 'place' * number of references to 'p') 
                Count<Place> rank = ranks.get(p.getElement());
                if (rank == null) {
                    rank = new Count<Place>(p.getElement());
                }
                rank.add(d.getCount() * p.getCount());
                ranks.put(p.getElement(), rank);
            }
        }
        
        // Sort
        List<Count<Place>> rankedCounts = new ArrayList<Count<Place>>(ranks.values());
        Collections.sort(rankedCounts);

        // And wrap into an ArrayList so we can return it properly
        List<Place> relatedPlaces = new ArrayList<Place>();
        int ct = 0;
        while (ct < limit && ct < rankedCounts.size()) {
            relatedPlaces.add(rankedCounts.get(ct).getElement());
            ct++;
        }
        return relatedPlaces;
    }

    @Override
    public List<GeoAnnotation> listReferencesTo(Place place) {
        Index<Node> index = getPlaceIndex();
        IndexHits<Node> hits = index.get(Place.KEY_URI, place.getURI());
        
        // Sanity check
        if (hits.size() == 0)
            throw new RuntimeException(new PlaceNotFoundException(place.getURI()));

        Node placeNode = hits.next();
        List<GeoAnnotation> records = new ArrayList<GeoAnnotation>();
        for (Relationship r : placeNode.getRelationships(PelagiosRelationships.REFERENCES)) {
            records.add(new GeoAnnotationImpl(r.getStartNode()));
        }

        return records;
    }

    @Override
    public List<Place> listCommonPlaces(List<Dataset> datasets) {
        if (datasets.size() == 0)
            return new ArrayList<Place>();

        if (datasets.size() == 1)
            return datasets.get(0).listPlaces(true);

        // TODO this can be made more efficient
        // TODO as a minimum, we could sort the datasets by number of places for improvement
        List<Place> sharedPlaces = new ArrayList<Place>(new HashSet<Place>(datasets.get(0).listPlaces(true)));
        for (int i = 1; i < datasets.size(); i++) {
            sharedPlaces = datasets.get(i).filterByReferenced(sharedPlaces, true);
        }
        return sharedPlaces;
    }

    @Override
    public Set<org.pelagios.graph.Path> findShortestPaths(Place from, Place to) {
        Node fromNode, toNode;

        Index<Node> idx = getPlaceIndex();
        IndexHits<Node> hits = idx.get(Place.KEY_URI, from.getURI());
        
        // Sanity check
        if (hits.size() == 0)
            throw new RuntimeException(new PlaceNotFoundException(from.getURI()));
        fromNode = hits.next();
        
        hits = idx.get(Place.KEY_URI, to.getURI());
        
        // Sanity check
        if (hits.size() == 0)
            throw new RuntimeException(new PlaceNotFoundException(to.getURI()));
        toNode = hits.next();

        // Run the shortest Path algorithm
        PathFinder<Path> pFinder = GraphAlgoFactory.shortestPath(expander, 8);
        
        // Wrap the Neo4j paths into the Pelagios API
        // TODO this is now a quick hack that needs total revision, it also
        // dates back to the time where a GeoAnnotations was allowed to
        // reference multiple places!
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
        // TODO this should be done in a more typesafe manner
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

        throw new RuntimeException("Graph inconsistency: unkown node type encountered: " + node);
    }

    @Override
    public void shutdown() {
        graphDb.shutdown();
    }

}
