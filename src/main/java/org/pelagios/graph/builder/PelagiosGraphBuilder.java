package org.pelagios.graph.builder;

import java.util.HashMap;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.pelagios.graph.PelagiosRelationships;

/**
 * The class contains the logic needed to initialize and/or instantiate
 * a Neo4j-based PelagiosGraph.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class PelagiosGraphBuilder {

    private String neo4jDir;

    public PelagiosGraphBuilder(String neo4jDir) {
        this.neo4jDir = neo4jDir;
    }

    public PelagiosGraphImpl build() {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase(neo4jDir);
        Transaction tx = graphDb.beginTx();
        try {
            // Initialize sub-reference nodes
            Node datasetSubreference = null;
            Node placeSubreference = null;
            for (Relationship r : graphDb.getReferenceNode().getRelationships()) {
                if (r.isType(PelagiosRelationships.DATASETS)) {
                    datasetSubreference = r.getEndNode();
                } else if (r.isType(PelagiosRelationships.PLACES)) {
                    placeSubreference = r.getEndNode();
                }
            }

            if (datasetSubreference == null)
                datasetSubreference = createSubreferenceNode(graphDb, PelagiosRelationships.DATASETS);

            if (placeSubreference == null)
                placeSubreference = createSubreferenceNode(graphDb, PelagiosRelationships.PLACES);

            HashMap<PelagiosRelationships, Node> subreferences = new HashMap<PelagiosRelationships, Node>();
            subreferences.put(PelagiosRelationships.DATASETS, datasetSubreference);
            subreferences.put(PelagiosRelationships.PLACES, placeSubreference);

            // Initialize indexes
            IndexManager im = graphDb.index();
            HashMap<String, Index<Node>> indexes = new HashMap<String, Index<Node>>();
            indexes.put(PelagiosGraphImpl.DATASET_INDEX, im.forNodes(PelagiosGraphImpl.DATASET_INDEX));
            indexes.put(PelagiosGraphImpl.PLACE_INDEX, im.forNodes(PelagiosGraphImpl.PLACE_INDEX));

            tx.success();
            return new PelagiosGraphImpl(graphDb, subreferences, indexes);
        } finally {
            tx.finish();
        }
    }

    private Node createSubreferenceNode(GraphDatabaseService graphDb, PelagiosRelationships r) {
        Node node = graphDb.createNode();
        graphDb.getReferenceNode().createRelationshipTo(node, r);
        return node;
    } 

}
