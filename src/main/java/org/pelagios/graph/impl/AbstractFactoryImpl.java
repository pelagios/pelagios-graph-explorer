package org.pelagios.graph.impl;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

/**
 * An abstract base class for Pelagios graph node
 * factory implementations.
 * 
 * @author Rainer Simon
 */
public class AbstractFactoryImpl {

	/**
	 * Reference to the Graph DB
	 */
    protected final GraphDatabaseService graphDb;

    /**
     * Reference to the index for this graph node type
     */
	protected Index<Node> index; 
    
    public AbstractFactoryImpl(GraphDatabaseService graphDb, Index<Node> index) {
    	this.graphDb = graphDb;
    	this.index = index;
    }
	
}
