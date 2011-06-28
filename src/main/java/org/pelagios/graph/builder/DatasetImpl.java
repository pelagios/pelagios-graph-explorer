package org.pelagios.graph.builder;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.pelagios.graph.Dataset;
import org.pelagios.graph.PelagiosRelationships;
import org.pelagios.graph.Place;

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

	public List<Place> listPlaces() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
