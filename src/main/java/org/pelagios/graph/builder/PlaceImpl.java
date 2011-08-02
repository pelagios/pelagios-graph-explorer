package org.pelagios.graph.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.pelagios.graph.PelagiosGraphNode;
import org.pelagios.graph.PelagiosRelationships;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;
import org.pelagios.io.geojson.GeoJSONParser;
import org.pelagios.io.geojson.GeoJSONSerializer;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Default (Neo4j-based) implementation of the PELAGIOS Place interface.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
class PlaceImpl extends PelagiosGraphNode implements Place {

    // We'll keep some properties in memory to avoid excessive DB transactions
    private URI memCachedURI = null;
    private Geometry memCachedGeometry = null;

    PlaceImpl(Node backingNode) {
        super(backingNode);
    }

    public String getLabel() {
        return getAsString(Place.KEY_LABEL);
    }

    public void setLabel(String label) {
        set(Place.KEY_LABEL, label);
    }

    public URI getURI() {
        try {
            if (memCachedURI == null)
                memCachedURI = new URI(getAsString(Place.KEY_URI));

            return memCachedURI;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setURI(URI uri) {
        memCachedURI = uri;
        set(Place.KEY_URI, uri.toString());
    }

    public Geometry getGeometry() {
        if (memCachedGeometry == null) {
            GeoJSONParser parser = new GeoJSONParser();
            memCachedGeometry = parser.parse(getAsString(Place.KEY_GEOMETRY));
        }

        return memCachedGeometry;
    }

    public void setGeometry(Geometry geometry) {
        memCachedGeometry = geometry;
        set(KEY_GEOMETRY, new GeoJSONSerializer().toString(geometry));
    }

    public List<GeoAnnotation> listGeoAnnotations() {
        List<GeoAnnotation> annotations = new ArrayList<GeoAnnotation>();
        for (Relationship r : backingNode.getRelationships(PelagiosRelationships.REFERENCES)) {
            annotations.add(new GeoAnnotationImpl(r.getStartNode()));
        }
        return annotations;
    }

    @Override
    public boolean equals(Object arg) {
        // Places are considered equal if their URIs are equal
        if (!(arg instanceof PlaceImpl))
            return false;

        PlaceImpl other = (PlaceImpl) arg;
        return getURI().equals(other.getURI());
    }

    @Override
    public int hashCode() {
        return getURI().hashCode();
    }

    @Override
    public String toString() {
        return "PLACE: " + getLabel() + " - " + getURI();
    }

    @Override
    public NodeType getType() {
        return NodeType.PLACE;
    }

    Node getBackingNode() {
        return backingNode;
    }

}
