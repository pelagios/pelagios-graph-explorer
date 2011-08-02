package org.pelagios.graph.nodes;

import java.net.URI;
import java.util.List;

/**
 * The Pelagios GeoAnnotation interface.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public interface GeoAnnotation {

    // String constants    
    public static final String KEY_URI = "uri";
    public static final String KEY_LABEL = "label";
    public static final String KEY_PROPERTIES = "properties";

    public URI getTargetURI();

    public String getLabel();

    public String getProperty(String key);

    public List<String> getPropertyKeys();

    /**
     * Returns the place referenced by the annotation body URI
     * @return the place
     */
    public Place getPlace();

    /**
     * Returns the (lowest-level) data (sub-)set this data record belongs to. 
     * @return the parent data set
     */
    public Dataset getParentDataset();

}
