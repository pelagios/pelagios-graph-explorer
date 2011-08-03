package org.pelagios.graph.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.pelagios.graph.PelagiosGraphNode;
import org.pelagios.graph.PelagiosRelationships;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

/**
 * Default (Neo4j-based) implementation of the PELAGIOS Dataset interface.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
class DatasetImpl extends PelagiosGraphNode implements Dataset {

    DatasetImpl(Node backingNode) {
        super(backingNode);
    }

    public String getName() {
        return getAsString(Dataset.KEY_NAME);
    }

    void setName(String name) {
        set(Dataset.KEY_NAME, name);
    }

    public Dataset getParentDataset() {
        Dataset parent = null;

        for (Relationship r : backingNode.getRelationships(Direction.OUTGOING, PelagiosRelationships.IS_SUBSET_OF)) {
            parent = new DatasetImpl(r.getEndNode());
        }

        if (parent == null)
            return this;
        
        return parent;
    }

    public Dataset getRootDataset() {
        return findRoot(this);
    }

    private Dataset findRoot(Dataset dataset) {
        Dataset parent = dataset.getParentDataset();
        if (parent.equals(dataset))
            return dataset;

        return findRoot(parent);
    }

    public boolean hasSubsets() {
        Iterable<Relationship> rels = backingNode.getRelationships(Direction.INCOMING,
                PelagiosRelationships.IS_SUBSET_OF);
        return rels.iterator().hasNext();
    }

    public List<Dataset> listSubsets() {
        List<Dataset> subsets = new ArrayList<Dataset>();

        for (Relationship r : backingNode.getRelationships(Direction.INCOMING, PelagiosRelationships.IS_SUBSET_OF)) {
            Node subsetNode = r.getStartNode();
            subsets.add(new DatasetImpl(subsetNode));
        }

        return subsets;
    }

    public boolean isSubsetOf(Dataset d) {
        return (d.listSubsets().contains(this));
    }

    public List<GeoAnnotation> listGeoAnnotations(boolean includeSubsets) {
        List<GeoAnnotation> records = new ArrayList<GeoAnnotation>();

        for (Relationship r : backingNode.getRelationships(PelagiosRelationships.GEOANNOTATION)) {
            records.add(new GeoAnnotationImpl(r.getEndNode()));
        }

        if (includeSubsets) {
            for (Dataset subset : listSubsets()) {
                records.addAll(subset.listGeoAnnotations(true));
            }
        }

        return records;
    }

    public List<Place> listPlaces(boolean includeSubsets) {
        return listPlaces(includeSubsets, true);
    }

    private List<Place> listPlaces(boolean includeSubsets, boolean unique) {
        Collection<Place> places;
        if (unique) {
            places = new HashSet<Place>();
        } else {
            places = new ArrayList<Place>();
        }

        for (GeoAnnotation r : listGeoAnnotations(includeSubsets)) {
            places.add(r.getPlace());
        }

        return new ArrayList<Place>(places);
    }

    public int countReferencesTo(Place place, boolean includeSubsets) {
        // TODO this could be done more efficiently directly in the graph!
        int ctr = 0;
        for (Place p : listPlaces(includeSubsets, false)) {
            if (p.equals(place))
                ctr++;
        }
        return ctr;
    }

    public List<Place> filterByReferenced(List<Place> places, boolean includeSubsets) {
        Set<Place> filtered = new HashSet<Place>();
        for (Place p : listPlaces(includeSubsets, false)) {
            if (places.contains(p))
                filtered.add(p);
        }
        return new ArrayList<Place>(filtered);
    }

    @Override
    public boolean equals(Object arg) {
        if (!(arg instanceof DatasetImpl))
            return false;

        // TODO names are a horrible way to identify datasets -> needs to be
        // changed!
        DatasetImpl other = (DatasetImpl) arg;
        return getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return "DATASET: " + getName();
    }

    @Override
    public NodeType getType() {
        return NodeType.DATASET;
    }

    Node getBackingNode() {
        return backingNode;
    }

}
