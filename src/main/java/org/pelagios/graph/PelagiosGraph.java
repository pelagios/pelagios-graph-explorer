package org.pelagios.graph;

import java.net.URI;
import java.util.List;

import org.pelagios.graph.builder.DataRecordBuilder;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.builder.PlaceBuilder;
import org.pelagios.graph.exception.DatasetExistsException;
import org.pelagios.graph.exception.DatasetNotFoundException;
import org.pelagios.graph.exception.PlaceExistsException;
import org.pelagios.graph.exception.PlaceNotFoundException;

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
	 */
	public void addDataset(DatasetBuilder dataset, DatasetBuilder parent)
		throws DatasetExistsException, DatasetNotFoundException;
	
	/**
	 * Retrieves the data set with the specified name.
	 * @param name the name
	 * @return the data set
	 */
	public Dataset getDataset(String name) throws DatasetNotFoundException;
	
	/**
	 * Adds a list of data records to the graph, belonging to the
	 * specified data set.
	 * @param records the data record
	 * @param parent the parent data set
	 * @throws DatasetNotFoundException if the parent data set was not found in the graph
	 * @throws PlaceNotFoundException if records contain references to places not in the graph
	 */
	public void addDataRecords(List<DataRecordBuilder> records, DatasetBuilder parent)
		throws DatasetNotFoundException;
	
	/**
	 * Adds a list of places to the graph.
	 * @param place the places
	 */
	public void addPlaces(List<PlaceBuilder> places) throws PlaceExistsException;
	
	/**
	 * Find a place by URI.
	 * @param uri the URI
	 * @return the place
	 */
	public Place getPlace(URI uri) throws PlaceNotFoundException;
	
	/**
	 * Returns an iterator with all the places in the graph.
	 * @return the places
	 */
	public Iterable<Place> listPlaces();
	
	/**
	 * Disconnects the graph DB.
	 */
	public void shutdown();
	
	/**
	 * Deletes the entire graph (use with caution!)
	 */
	public void clear();
	
}
