package org.pelagios.graph;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * An abstract base class for PELAGIOS graph nodes.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public abstract class PelagiosGraphNode {

    public enum NodeType {
        DATASET {
            public String toString() {
                return "Dataset";
            }
        },
        GEOANNOTATION {
            public String toString() {
                return "GeoAnnotation";
            }
        },
        PLACE {
            public String toString() {
                return "Place";
            }
        }
    }

    protected Node backingNode;

    protected Logger log = Logger.getLogger(PelagiosGraphNode.class);

    public PelagiosGraphNode(Node backingNode) {
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

    public abstract NodeType getType();

}
