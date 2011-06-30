package org.pelagios.graph.builder;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * An abstract base class for Pelagios graph node
 * implementations.
 * 
 * @author Rainer Simon
 */
class AbstractNodeImpl {
	
	Node backingNode;
	
	protected Logger log = Logger.getLogger(AbstractNodeImpl.class);
	
	public AbstractNodeImpl(Node backingNode) {
		this.backingNode = backingNode;
	}
	
	protected Object get(String property) {
		return this.backingNode.getProperty(property);
	}
	
	protected String getAsString(String property) {
		return (String) this.backingNode.getProperty(property);
	}
	
	protected double getAsDouble(String property) {
		return Double.parseDouble(getAsString(property));
	}
	
	protected void set(String key, Object value) {
		this.backingNode.setProperty(key, value);
	}

}
