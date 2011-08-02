package org.pelagios.graph.nodes;

import java.net.URI;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

/**
 * The PELAGIOS Place interface.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public interface Place {

    // String constants
    public static final String KEY_LABEL = "label";
    public static final String KEY_URI = "uri";
    public static final String KEY_GEOMETRY = "geometry";

    public String getLabel();

    public URI getURI();

    public Geometry getGeometry();

    /**
     * Lists all GeoAnnotations that reference this place.
     * @return the GeoAnnotations
     */
    public List<GeoAnnotation> listGeoAnnotations();

}
