package org.pelagios.graph.nodes;

import java.util.List;

/**
 * The PELAGIOS Dataset interface.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public interface Dataset {

    // String constants
    public static final String KEY_NAME = "name";
    public static final String KEY_GEOANNOTATIONS = "geoAnnotations";
    public static final String KEY_PLACES = "places";

    public String getName();

    public Dataset getParentDataset();

    public Dataset getRootDataset();

    public boolean hasSubsets();

    public List<Dataset> listSubsets();

    public boolean isSubsetOf(Dataset d);

    /**
     * Returns a list of all GeoAnnotations contained in this data set. If
     * <code>includeSubsets</code> is set to <code>true</code> the method
     * will iterate through all the subsets as well.
     * @param includeSubsets if <code>true</code>, the method will iterate through
     * all the subsets of this dataset
     * @return the list of GeoAnnotations
     */
    public List<GeoAnnotation> listGeoAnnotations(boolean includeSubsets);

    /**
     * Lists all places referenced by annotations in this data set. If
     * <code>includeSubsets</code> is set to <code>true</code> the method
     * will iterate through all the subsets as well.
     * @param includeSubsets if <code>true</code>, the method will iterate through
     * all the subsets of this dataset
     * @return the places
     */
    public List<Place> listPlaces(boolean includeSubsets);

    /**
     * Counts how often the specified place is referenced by annotations in this data
     * set. If <code>includeSubsets</code> is set to <code>true</code> the method
     * will iterate through all the subsets as well.
     * @param place the place
     * @param includeSubsets if <code>true</code>, the method will iterate through
     * all the subsets of this dataset
     * @return the number of times the place is referenced in the data set
     */
    public int countReferencesTo(Place place, boolean includeSubsets);

    /**
     * Checks all places in the given list, and returns a list which contains
     * only those places which are referenced in this data set.
     * @param places a list of places to filter
     * @param includeSubsets if <code>true</code>, the method will iterate through
     * all the subsets of this dataset
     * @return the places from the list which are referenced in this data set
     */
    public List<Place> filterByReferenced(List<Place> places, boolean includeSubsets);

}
