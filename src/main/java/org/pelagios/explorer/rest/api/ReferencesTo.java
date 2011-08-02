package org.pelagios.explorer.rest.api;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This API domain object encapsulates basic information about the number of 
 * references to a Place in one particular data set (i.e. how many GeoAnnotations
 * in the data set reference the Place).
 * 
 * @author Rainer Simon
 */
public class ReferencesTo {

    /**
     * The place URI
     */
    String place;

    /**
     * The data set
     */
    String dataset;

    /**
     * The total number of GeoAnnotations in the data set
     */
    int datasetSize;

    /**
     * The dataset footprint
     */
    Geometry datasetFootprint;

    /**
     * The root data set of the data set, e.g. 'Ptolemy Machine' for 'Ptolemy
     * Machine 5:14'
     */
    String rootDataset;

    /**
     * The number of occurences in the data set, i.e. GeoAnnotations that
     * reference the place
     */
    int referencesTo;

    public ReferencesTo(String place, String dataset, int datasetSize, Geometry datasetFootprint, String rootDataset,
            int referencesTo) {

        this.place = place;
        this.dataset = dataset;
        this.datasetSize = datasetSize;
        this.datasetFootprint = datasetFootprint;
        this.rootDataset = rootDataset;
        this.referencesTo = referencesTo;
    }

}
