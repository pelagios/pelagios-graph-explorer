package org.pelagios.explorer.rest.api;

import java.util.List;

import org.pelagios.graph.nodes.Place;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This API domain object describes the place-wise overlap between two data sets.

 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class Overlap {

    /**
     * The source dataset name
     */
    String srcSet;

    /**
     * The target dataset name
     */
    String destSet;

    /**
     * The number of places shared among the linked data sets
     */
    List<Place> commonPlaces;

    /**
     * The geographical area covered by the overlap
     */
    Geometry footprint;

    public Overlap(String srcSet, String destSet, List<Place> commonPlaces, Geometry footprint) {
        this.srcSet = srcSet;
        this.destSet = destSet;
        this.commonPlaces = commonPlaces;
        this.footprint = footprint;
    }

}
