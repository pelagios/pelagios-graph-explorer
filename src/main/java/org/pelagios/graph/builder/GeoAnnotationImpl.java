package org.pelagios.graph.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.pelagios.graph.PelagiosGraphNode;
import org.pelagios.graph.PelagiosRelationships;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

class GeoAnnotationImpl extends PelagiosGraphNode implements GeoAnnotation {

    public GeoAnnotationImpl(Node backingNode) {
        super(backingNode);
    }

    void setTargetURI(URI uri) {
        set(GeoAnnotation.KEY_URI, uri.toString());
    }

    public URI getTargetURI() {
        try {
            return new URI(getAsString(GeoAnnotation.KEY_URI));
        } catch (URISyntaxException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
    }

    public void setLabel(String label) {
        set(GeoAnnotation.KEY_LABEL, label);
    }
   
    public String getLabel() {
        return getAsString(GeoAnnotation.KEY_LABEL);
    }

    public void addProperty(String key, String value) {
        set(key, value);
    }

    public String getProperty(String key) {
        return getAsString(key);
    }

    public List<String> getPropertyKeys() {
        List<String> commonProperties = Arrays.asList(GeoAnnotation.KEY_URI, GeoAnnotation.KEY_LABEL);

        List<String> properties = new ArrayList<String>();
        for (String key : backingNode.getPropertyKeys()) {
            if (!commonProperties.contains(key))
                properties.add(key);
        }
        return properties;
    }

    boolean setPlace(URI uri, Index<Node> placeIndex) {
        boolean success = true;
        IndexHits<Node> hits = placeIndex.get(Place.KEY_URI, uri);
        if (hits.size() == 0) {
            log.warn("Place " + uri.toString() + " not in graph - skipping this reference");
            success = false;
        } else {
            Node placeNode = hits.next();
            backingNode.createRelationshipTo(placeNode, PelagiosRelationships.REFERENCES);
        }
        return success;
    }
    
    public Place getPlace() {
        Place p = null;
        for (Relationship r : backingNode.getRelationships(PelagiosRelationships.REFERENCES)) {
            p = new PlaceImpl(r.getEndNode());
        }
        
        // Sanity check
        if (p == null)
            throw new RuntimeException("Graph inconsitency: annotation does not reference a place");
        
        return p;
    }

    public Dataset getParentDataset() {
        Dataset d = null;
        int ctr = 0;
        for (Relationship r : backingNode.getRelationships(PelagiosRelationships.GEOANNOTATION)) {
            // Can only have 1 relationship of type RECORD
            d = new DatasetImpl(r.getStartNode());
            ctr++;
        }
        
        // Sanity checks
        if (ctr > 1)
            throw new RuntimeException("Graph inconsistency: annotation referenced by " + ctr +  " datasets");
        
        if (d == null)
            throw new RuntimeException("Graph inconsistency: annotation not referenced by any dataset");
        
        return d;
    }

    @Override
    public String toString() {
        return "GEOANNOTATION: " + getTargetURI();
    }

    @Override
    public NodeType getType() {
        return NodeType.GEOANNOTATION;
    }

    Node getBackingNode() {
        return backingNode;
    }

}
