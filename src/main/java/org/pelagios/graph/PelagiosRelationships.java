package org.pelagios.graph;

import org.neo4j.graphdb.RelationshipType;

/**
 * Relations in the Pelagios graph
 * 
 * TODO document the graph structure
 * 
 * @author Rainer Simon
 */
public enum PelagiosRelationships implements RelationshipType {
	
	// The relation between the reference node and
	// all top-level data sets
	DATASETS, PLACES,
	
	// Dataset relations
	DATASET, IS_SUBSET_OF, RECORD,
	
	// Place relations
	PLACE,
	
	// Relation between data record and place 
	REFERENCES
	
}
