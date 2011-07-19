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
 * The PelagiosGraph interface.
 * 
 * @author Rainer Simon
 */
public abstract class PelagiosGraph {
	
	private static PelagiosGraph instance = null;
	
	/**
	 * TODO implement this!
	 * @return
	 */
	public static PelagiosGraph getInstance() {
		if (instance == null) {
			PelagiosGraphBuilder builder = 
				new PelagiosGraphBuilder(Config.getInstance().getNeo4jDirectory());
			instance = builder.build();
		}
			
		return instance;
	}
	
	/**
	 * Returns all top-level data sets in the graph.
	 * @return the top-level data sets
	 */
	public abstract List<Dataset> listTopLevelDatasets();
	
	/**
	 * Adds a top-level data set to the graph.
	 * @param dataset the data set
	 */
	public abstract void addDataset(DatasetBuilder dataset)
		throws DatasetExistsException;
	
	/**
	 * Adds a data sub-set to the graph, with the
	 * specified parent data set.
	 * @param dataset the data set
	 * @param parent the parent data set (i.e. super set)
	 * @throws DatasetExitsException if the data set is already in the graph
	 * @throws DatasetNotFoundException if the parent set is not in the graph
	 */
	public abstract void addDataset(DatasetBuilder dataset, DatasetBuilder parent)
		throws DatasetExistsException, DatasetNotFoundException;
	
	/**
	 * Retrieves the data set with the specified name.
	 * @param name the name
	 * @return the data set
	 * @throws DatasetNotFoundException if the data set is not in the graph
	 */
	public abstract Dataset getDataset(String name) throws DatasetNotFoundException;
	
	/**
	 * Adds a list of data records to the graph, belonging to the
	 * specified data set.
	 * @param records the data record
	 * @param parent the parent data set
	 * @throws DatasetNotFoundException if the parent data set was not found in the graph
	 */
	public abstract void addDataRecords(List<GeoAnnotationBuilder> records, DatasetBuilder parent)
		throws DatasetNotFoundException;
	
	/**
	 * Adds a list of places to the graph. Places are unique. I.e. if the
	 * list contains a place that's already in the graph, this method will
	 * fail with a PlaceExistsException. The transaction will be rolled back.
	 * @param place the places
	 * @throws PlaceExistsException if a place in the list exists in the graph
	 */
	public abstract void addPlaces(List<PlaceBuilder> places)
		throws PlaceExistsException;
	
	/**
	 * Find a place by URI.
	 * @param uri the URI
	 * @return the place
	 * @throws PlaceNotFoundException if the place is not in the graph
	 */
	public abstract Place getPlace(URI uri) throws PlaceNotFoundException;
	
	public abstract List<Place> searchPlaces(String prefix, int limit);
	
	/**
	 * Returns an iterator with all the places in the graph.
	 * @return the places
	 */
	public abstract Iterable<Place> listPlaces();
	
	/**
	 * Returns all data records that reference the specified place. 
	 * @param place the place
	 * @return the data records
	 * @throws PlaceNotFoundException if the place was not found in the graph
	 */
	public abstract List<GeoAnnotation> listReferencesTo(Place place)
		throws PlaceNotFoundException;
	
	/**
	 * Returns the list of shared places in the specified
	 * data sets, i.e. all places which appear in each of
	 * them.
	 * @param datasets the data sets
	 * @return the shared places
	 */
	public abstract List<Place> listSharedPlaces(List<Dataset> datasets);
	
	/**
	 * Performs a shortest path search between two places in the graph.
	 * Note: unfortunately we're breaking the API a bit here, since we're
	 * working with neo4j paths/nodes directly instead of Pelagios
	 * domain objects. 
	 * TODO needs revision!
	 * 
	 * @param from the starting place
	 * @param to the destination place
	 * @return the shortest path(s)
	 * @throws PlaceNotFoundException if at least one of the places was not found
	 * in the graph
	 */
	public abstract Set<Path> findShortestPaths(Place from, Place to) throws PlaceNotFoundException;
	
	/**
	 * Be kind. Always disconnect the graph DB before leaving.
	 */
	public abstract void shutdown();
	
}
