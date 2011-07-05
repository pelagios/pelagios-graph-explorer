package org.pelagios.backend.graph;

import java.net.URI;
import java.util.List;

import org.pelagios.backend.graph.builder.DataRecordBuilder;
import org.pelagios.backend.graph.builder.DatasetBuilder;
import org.pelagios.backend.graph.builder.PlaceBuilder;
import org.pelagios.backend.graph.exception.DatasetExistsException;
import org.pelagios.backend.graph.exception.DatasetNotFoundException;
import org.pelagios.backend.graph.exception.PlaceExistsException;
import org.pelagios.backend.graph.exception.PlaceNotFoundException;

/**
 * The PelagiosGraph interface.
 * 
 * @author Rainer Simon
 */
public interface PelagiosGraph {
	
	/**
	 * Returns all top-level data sets in the graph.
	 * @return the top-level data sets
	 */
	public List<Dataset> listTopLevelDatasets();
	
	/**
	 * Adds a top-level data set to the graph.
	 * @param dataset the data set
	 */
	public void addDataset(DatasetBuilder dataset)
		throws DatasetExistsException;
	
	/**
	 * Adds a data sub-set to the graph, with the
	 * specified parent data set.
	 * @param dataset the data set
	 * @param parent the parent data set (i.e. super set)
	 * @throws DatasetExitsException if the data set is already in the graph
	 * @throws DatasetNotFoundException if the parent set is not in the graph
	 */
	public void addDataset(DatasetBuilder dataset, DatasetBuilder parent)
		throws DatasetExistsException, DatasetNotFoundException;
	
	/**
	 * Retrieves the data set with the specified name.
	 * @param name the name
	 * @return the data set
	 * @throws DatasetNotFoundException if the data set is not in the graph
	 */
	public Dataset getDataset(String name) throws DatasetNotFoundException;
	
	/**
	 * Adds a list of data records to the graph, belonging to the
	 * specified data set.
	 * @param records the data record
	 * @param parent the parent data set
	 * @throws DatasetNotFoundException if the parent data set was not found in the graph
	 */
	public void addDataRecords(List<DataRecordBuilder> records, DatasetBuilder parent)
		throws DatasetNotFoundException;
	
	/**
	 * Adds a list of places to the graph. Places are unique. I.e. if the
	 * list contains a place that's already in the graph, this method will
	 * fail with a PlaceExistsException. The transaction will be rolled back.
	 * @param place the places
	 * @throws PlaceExistsException if a place in the list exists in the graph
	 */
	public void addPlaces(List<PlaceBuilder> places)
		throws PlaceExistsException;
	
	/**
	 * Find a place by URI.
	 * @param uri the URI
	 * @return the place
	 * @throws PlaceNotFoundException if the place is not in the graph
	 */
	public Place getPlace(URI uri) throws PlaceNotFoundException;
	
	/**
	 * Returns an iterator with all the places in the graph.
	 * @return the places
	 */
	public Iterable<Place> listPlaces();
	
	/**
	 * Returns the list of shared places in the specified
	 * data sets, i.e. all places which appear in each of
	 * them.
	 * @param datasets the data sets
	 * @return the shared places
	 */
	public List<Place> listSharedPlaces(List<Dataset> datasets);
	
	/**
	 * Be kind. Always disconnect the graph DB before leaving.
	 */
	public void shutdown();
	
}
