package org.pelagios.graph;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.pelagios.explorer.Config;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.builder.GeoAnnotationBuilder;
import org.pelagios.graph.builder.PelagiosGraphBuilder;
import org.pelagios.graph.builder.PlaceBuilder;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.exceptions.DatasetNotFoundException;
import org.pelagios.graph.exceptions.PlaceExistsException;
import org.pelagios.graph.exceptions.PlaceNotFoundException;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

/**
 * The PelagiosGraph abstract class.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public abstract class PelagiosGraph {

    // The singleton default DB instance (as configured in config.properties)
    private static PelagiosGraph defaultDBInstance = null;

    public static PelagiosGraph getDefaultDB() {
        if (defaultDBInstance == null) {
            PelagiosGraphBuilder builder = new PelagiosGraphBuilder(Config.getInstance().getNeo4jDirectory());
            defaultDBInstance = builder.build();
        }

        return defaultDBInstance;
    }

    public abstract List<Dataset> listTopLevelDatasets();

    /**
     * Adds a top-level data set to the graph. 
     * @param dataset the data set
     * @throws DatasetExitsException if the data set is already in the graph
     */
    public abstract void addDataset(DatasetBuilder dataset) throws DatasetExistsException;

    /**
     * Adds a data sub-set to the graph, with the specified parent data set.
     * @param dataset the data set to add
     * @param parent the parent data set 
     * @throws DatasetExitsException if the data set is already in the graph
     * @throws DatasetNotFoundException if the parent set is not in the graph
     */
    public abstract void addDataset(DatasetBuilder dataset, DatasetBuilder parent)
        throws DatasetExistsException, DatasetNotFoundException;

    public abstract Dataset getDataset(String name) throws DatasetNotFoundException;

    public abstract void addGeoAnnotations(List<GeoAnnotationBuilder> records, DatasetBuilder parent)
            throws DatasetNotFoundException;

    /**
     * Adds a list of places to the graph. Places are unique. I.e. if the list
     * contains a place that's already in the graph, this method will fail with
     * a PlaceExistsException. The transaction will be rolled back.
     * @param places the places
     * @throws PlaceExistsException if a place in the list already exists in the graph
     */
    public abstract void addPlaces(List<PlaceBuilder> places) throws PlaceExistsException;

    public abstract Place getPlace(URI uri) throws PlaceNotFoundException;

    /**
     * Returns places which labels start with the specified prefix string. 
     * I.e. a prefix of 'aeg' will return places such as Aegae, Agyptium Mare,
     * Aegyptus, etc.
     * @param prefix the prefix
     * @param limit the maximum amount of places to return
     * @return the places
     */
    public abstract List<Place> searchPlaces(String prefix, int limit);

    /**
     * Returns an iterator with all the places in the graph.
     * @return the places
     */
    public abstract Iterable<Place> listPlaces();

    public abstract List<Place> findStronglyRelatedPlaces(Place place, int limit);

    /**
     * Returns all GeoAnnotations that reference the specified place.
     * @param place the place
     * @return the annotations
     */
    public abstract List<GeoAnnotation> listReferencesTo(Place place);

    /**
     * Lists the places that the specified data sets have in common, i.e. those
     * places which are referenced in each of them.
     * @param datasets the data sets
     * @return the places
     */
    public abstract List<Place> listCommonPlaces(List<Dataset> datasets);

    /**
     * Performs a shortest path search between two places in the graph.
     * @param from the starting place
     * @param to the destination place
     * @return the shortest paths
     */
    public abstract Set<Path> findShortestPaths(Place from, Place to);

    /**
     * Be kind. Always disconnect the graph DB before leaving.
     */
    public abstract void shutdown();

}
