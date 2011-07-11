package org.pelagios.backend.graph.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.pelagios.backend.graph.DataRecord;
import org.pelagios.backend.graph.Dataset;
import org.pelagios.backend.graph.PelagiosRelationships;
import org.pelagios.backend.graph.Place;

/**
 * Implementation of the Dataset graph node type.
 * 
 * @author Rainer Simon
 */
class DatasetImpl extends AbstractNodeImpl implements Dataset {

	DatasetImpl(Node backingNode) {
		super(backingNode);
	}
	
	public String getName() {
		return getAsString(Dataset.KEY_NAME);
	}

	void setName(String name) {
		set(Dataset.KEY_NAME, name);
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
	
	public List<DataRecord> listRecords() {
		List<DataRecord> records = new ArrayList<DataRecord>(); 
		
		for (Relationship r : backingNode.getRelationships(PelagiosRelationships.RECORD)) {
			records.add(new DataRecordImpl(r.getEndNode()));
		}
		
		for (Dataset subset : listSubsets()) {
			records.addAll(subset.listRecords());
		}
		
		return records;
	}

	public List<Place> listPlaces(boolean includeSubsets) {
		Set<Place> places = new HashSet<Place>(); 
		
		for (DataRecord r : listRecords()) {
			places.addAll(r.listPlaces());
		}
		
		if (includeSubsets) {
			for (Dataset subset : listSubsets()) {
				places.addAll(subset.listPlaces(true));
			}
		}
		
		return new ArrayList<Place>(places);
	}

	public boolean isPlaceReferenced(Place place, boolean includeSubsets) {
		List<Place> filtered = filterReferenced(Arrays.asList(place), includeSubsets);
		return filtered.size() != 0;
	}

	public List<Place> filterReferenced(List<Place> places, boolean includeSubsets) {
		List<Place> filtered = new ArrayList<Place>();
		for (Place p : listPlaces(includeSubsets)) {
			if (places.contains(p))
				filtered.add(p);
		}
		return filtered;
	}
	
	@Override
	public String toString() {
		return "DATASET: " + getName();
	}
	
}
