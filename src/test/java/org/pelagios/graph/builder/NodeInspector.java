package org.pelagios.graph.builder;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.pelagios.graph.Dataset;
import org.pelagios.graph.Place;

public class NodeInspector {

	public String toString(Place place) {
		return toString(((PlaceImpl) place).backingNode);
	}
	
	public String toString(Dataset dataset) {
		return toString(((DatasetImpl) dataset).backingNode);
	}
	
	private String toString(Node node) {
		StringBuffer sb = new StringBuffer();
		sb.append("node id: " + node.getId() + "\n");
		
		sb.append("node properties:\n");
		for (String key : node.getPropertyKeys()) {
			sb.append("  " + key + ": " + node.getProperty(key) + "\n");
		}
		
		sb.append("incoming relationships:\n");
		for (Relationship r : node.getRelationships(Direction.INCOMING)) {
			sb.append("  " + r.getType() + " (from node " + 
					r.getStartNode().getId() + ")\n");
		}

		sb.append("outgoing relationships:\n");
		for (Relationship r : node.getRelationships(Direction.OUTGOING)) {
			sb.append("  " + r.getType() + " (to node " + 
					r.getEndNode().getId() + ")\n");
		}
		
		return sb.toString();
	}
	
}
