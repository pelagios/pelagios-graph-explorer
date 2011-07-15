package org.pelagios.backend.graph.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.pelagios.backend.graph.AnnotationNode;
import org.pelagios.backend.graph.DatasetNode;
import org.pelagios.backend.graph.PelagiosRelationships;
import org.pelagios.backend.graph.PlaceNode;

/**
 * Implementation of the Dataset graph node type.
 * 
 * @author Rainer Simon
 */
class DatasetImpl extends AbstractNodeImpl implements DatasetNode {

	DatasetImpl(Node backingNode) {
		super(backingNode);
	}
	
	public String getName() {
		return getAsString(DatasetNode.KEY_NAME);
	}

	void setName(String name) {
		set(DatasetNode.KEY_NAME, name);
	}
	
	public DatasetNode getParent() {
		DatasetNode parent = null;
		
		for (Relationship r : backingNode.getRelationships(Direction.OUTGOING, PelagiosRelationships.IS_SUBSET_OF)) {
		    parent = new DatasetImpl(r.getEndNode());
		}
		
		return parent;
	}
	
	public DatasetNode getRoot() {
		return findRoot(this);
	}
	
	private DatasetNode findRoot(DatasetNode dataset) {
		DatasetNode parent = dataset.getParent();
		if (parent == null)
			return dataset;
		
		return findRoot(parent);
	}

	public boolean hasSubsets() {
		Iterable<Relationship> rels = backingNode
			.getRelationships(Direction.INCOMING, PelagiosRelationships.IS_SUBSET_OF);
		return rels.iterator().hasNext();
	}

	public List<DatasetNode> listSubsets() {
		List<DatasetNode> subsets = new ArrayList<DatasetNode>();
		
		for (Relationship r : backingNode.getRelationships(Direction.INCOMING, PelagiosRelationships.IS_SUBSET_OF)) {
		    Node subsetNode = r.getStartNode();
		    subsets.add(new DatasetImpl(subsetNode));
		}
		
		return subsets;
	}
	
	public boolean isSubsetOf(DatasetNode d) {
		return (d.listSubsets().contains(this));
	}
	
	public List<AnnotationNode> listRecords() {
		List<AnnotationNode> records = new ArrayList<AnnotationNode>(); 
		
		for (Relationship r : backingNode.getRelationships(PelagiosRelationships.RECORD)) {
			records.add(new DataRecordImpl(r.getEndNode()));
		}
		
		for (DatasetNode subset : listSubsets()) {
			records.addAll(subset.listRecords());
		}
		
		return records;
	}

	public List<PlaceNode> listPlaces(boolean includeSubsets) {
		return listPlaces(includeSubsets, true);
	}
	
	private List<PlaceNode> listPlaces(boolean includeSubsets, boolean unique) {
		Collection<PlaceNode> places;
		if (unique) {
			places = new HashSet<PlaceNode>();
		} else {
			places = new ArrayList<PlaceNode>();
		}
		
		for (AnnotationNode r : listRecords()) {
			places.addAll(r.listPlaces());
		}
		
		if (includeSubsets) {
			for (DatasetNode subset : listSubsets()) {
				places.addAll(subset.listPlaces(true));
			}
		}
		
		return new ArrayList<PlaceNode>(places);
	}

	public int countReferences(PlaceNode place, boolean includeSubsets) {
		List<PlaceNode> filtered = filterReferenced(Arrays.asList(place), includeSubsets);
		return filtered.size();
	}

	public List<PlaceNode> filterReferenced(List<PlaceNode> places, boolean includeSubsets) {
		List<PlaceNode> filtered = new ArrayList<PlaceNode>();
		for (PlaceNode p : listPlaces(includeSubsets, false)) {
			if (places.contains(p))
				filtered.add(p);
		}
		return filtered;
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
	
}
