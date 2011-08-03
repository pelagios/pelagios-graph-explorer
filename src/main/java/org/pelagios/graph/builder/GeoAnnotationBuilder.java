package org.pelagios.graph.builder;

import java.net.URI;
import java.util.HashMap;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.pelagios.graph.exceptions.PlaceNotFoundException;

/**
 * Builder class for the default (Neo4j-based) {@link GeoAnnotation} implementation.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class GeoAnnotationBuilder {

    private URI targetURI;
    
    private URI bodyURI;

    private String label;

    private HashMap<String, String> properties = new HashMap<String, String>();

    public GeoAnnotationBuilder(URI targetURI, URI bodyURI) {
        this.targetURI = targetURI;
        this.bodyURI = bodyURI;
    }
    
    public URI getTargetURI() {
        return targetURI;
    }
    
    public URI getBodyURI() {
        return bodyURI;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public GeoAnnotationImpl build(GraphDatabaseService graphDb, Index<Node> placeIndex) 
        throws PlaceNotFoundException {
        
        Node node = graphDb.createNode();
        try {
            GeoAnnotationImpl record = new GeoAnnotationImpl(node);
            record.setPlace(bodyURI, placeIndex);
            record.setTargetURI(targetURI);
            record.setLabel(label);
            
            for (String key : properties.keySet()) {
                record.addProperty(key, properties.get(key));
            }

            return record;
        } catch (PlaceNotFoundException e) {
            // Ouch - remove the node again
            node.delete();
            throw e;
        }
    }

}
