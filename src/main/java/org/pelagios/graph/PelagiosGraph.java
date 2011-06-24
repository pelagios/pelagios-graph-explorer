package org.pelagios.graph;

import java.net.URI;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.builder.PlaceBuilder;

/**
 * The PelagiosGraph interface.
 * 
 * @author Rainer Simon
 */
public interface PelagiosGraph {
	
	/**
	 * Returns the underlying graph DB
	 * @return the graph DB
	 */
	GraphDatabaseService getGraphDB();
	
	/**
	 * Returns the underlying dataset index
	 * @return the dataset index
	 */
	Index<Node> getDatasetIndex();
	
	/**
	 * Returns the underlying place index
	 * @return
	 */
	Index<Node> getPlaceIndex();
	
	/**
	 * Returns all top-level data sets in the graph.
	 * @return the top-level data sets
	 */
	public List<Dataset> listTopLevelDatasets();
	
	/**
	 * Adds a top-level data set to the graph.
	 * @param dataset the data set
	 */
	public void addDataset(DatasetBuilder dataset);
	
	/**
	 * Adds a data sub-set to the graph, with the
	 * specified parent data set.
	 * @param dataset the data set
	 * @param parent the parent data set (i.e. super set)
	 */
	public void addDataset(DatasetBuilder dataset, DatasetBuilder parent);
	
	/**
	 * Retrieves the data set with the specified name.
	 * @param name the name
	 * @return the data set
	 */
	public Dataset getDataset(String name);
	
	/**
	 * Adds a place to the graph.
	 * @param place
	 */
	public void addPlaces(List<PlaceBuilder> places);
	
	/**
	 * Find a place by URI.
	 * @param uri the URI
	 * @return the place
	 */
	public Place getPlace(URI uri);
	
	/**
	 * Disconnects the graph DB.
	 */
	public void shutdown();
	
	/**
	 * Deletes the entire graph (use with caution!)
	 */
	public void clear();
	
}
