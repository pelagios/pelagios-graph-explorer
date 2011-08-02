package org.pelagios.graph.builder;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.pelagios.graph.nodes.Dataset;

/**
 * Builder class for the default (Neo4j-based) {@link Dataset} implementation.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class DatasetBuilder {

    private String name;

    public DatasetBuilder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public DatasetImpl build(GraphDatabaseService graphDb, Index<Node> index) {
        Node node = graphDb.createNode();
        DatasetImpl dataset = new DatasetImpl(node);
        dataset.setName(name);
        index.add(node, Dataset.KEY_NAME, name);
        return dataset;
    }

}
