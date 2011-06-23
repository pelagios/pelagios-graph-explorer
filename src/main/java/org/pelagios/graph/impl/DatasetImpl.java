package org.pelagios.graph.impl;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.pelagios.graph.Dataset;
import org.pelagios.graph.PelagiosRelationships;

/**
 * Implementation of the Dataset graph node type.
 * 
 * @author Rainer Simon
 */
public class DatasetImpl extends AbstractNodeImpl implements Dataset {

	DatasetImpl(Node backingNode) {
		super(backingNode);
	}
	
	public String getName() {
		return getAsString(Dataset.KEY_NAME);
	}

	public void setName(String name) {
		set(Dataset.KEY_NAME, name);
	}

	public void appendSubset(Dataset child) {
		Transaction tx = backingNode.getGraphDatabase().beginTx();
		try {
			Node childNode = ((DatasetImpl) child).backingNode;
			backingNode.createRelationshipTo(childNode, PelagiosRelationships.IS_SUBSET_OF);
			childNode.createRelationshipTo(backingNode, PelagiosRelationships.IS_SUPERSET_OF);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	public boolean hasSubsets() {
		Iterable<Relationship> rels = backingNode
			.getRelationships(Direction.INCOMING, PelagiosRelationships.IS_SUPERSET_OF);
		return rels.iterator().hasNext();
	}

	public List<Dataset> listSubsets() {
		List<Dataset> subsets = new ArrayList<Dataset>();
		
		for (Relationship r : backingNode.getRelationships(Direction.INCOMING, PelagiosRelationships.IS_SUPERSET_OF)) {
		    Node subsetNode = r.getStartNode();
		    subsets.add(new DatasetImpl(subsetNode));
		}
		
		return subsets;
	}
	
}
