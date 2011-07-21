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
 * Implementation of the Dataset graph node type.
 * 
 * @author Rainer Simon
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
	
	public Dataset getParent() {
		Dataset parent = null;
		
		for (Relationship r : backingNode.getRelationships(Direction.OUTGOING, PelagiosRelationships.IS_SUBSET_OF)) {
		    parent = new DatasetImpl(r.getEndNode());
		}
		
		return parent;
	}
	
	public Dataset getRoot() {
		return findRoot(this);
	}
	
	private Dataset findRoot(Dataset dataset) {
		Dataset parent = dataset.getParent();
		if (parent == null)
			return dataset;
		
		return findRoot(parent);
	}

	public boolean hasSubsets() {
		Iterable<Relationship> rels = backingNode
			.getRelationships(Direction.INCOMING, PelagiosRelationships.IS_SUBSET_OF);
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
		
		for (Relationship r : backingNode.getRelationships(PelagiosRelationships.RECORD)) {
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
			places.addAll(r.listPlaces());
		}
		
		return new ArrayList<Place>(places);
	}

	public int countReferences(Place place, boolean includeSubsets) {
		// A quick hack...
		int ctr = 0;
		for (Place p : listPlaces(includeSubsets, false)) {
			if (p.equals(place))
				ctr++;
		}
		return ctr;	
	}

	public List<Place> filterReferenced(List<Place> places, boolean includeSubsets) {
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
		
		// TODO names are a horrible way to identify datasets -> needs to be changed!
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
