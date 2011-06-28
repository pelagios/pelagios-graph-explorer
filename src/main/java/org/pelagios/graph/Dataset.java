package org.pelagios.graph;

import java.util.List;

/**
 * The Pelagios Dataset interface.
 * 
 * TODO probably we should return iterators instead of lists
 * for the listSubsets() and listPlaces() methods.
 * 
 * @author Rainer Simon
 */
public interface Dataset {
	
	/**
	 * String constants
	 */
	public static final String KEY_NAME = "name";
	
	/**
	 * Returns the name of this dataset.
	 * @return the name
	 */
	public String getName();
		
	/**
	 * Checks wether this data set has any sub-sets attached to it.
	 * @return true in case there are sub-sets
	 */
	public boolean hasSubsets();
	
	/**
	 * Lists all sub sets of contained in this data set
	 * @return the sub sets 
	 */
	public List<Dataset> listSubsets();
	
	/**
	 * Lists all places contained in this data set
	 * @return the places
	 */
	public List<Place> listPlaces();
		
}
