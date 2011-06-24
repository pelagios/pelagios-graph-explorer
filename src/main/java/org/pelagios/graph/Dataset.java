package org.pelagios.graph;

import java.util.List;

/**
 * The Pelagios Dataset interface.
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
	 * Sets the name for this dataset.
	 */
	public void setName(String name);
		
	/**
	 * Checks wether this data set has any sub-sets attached to it.
	 * @return true in case there are sub-sets
	 */
	public boolean hasSubsets();
	
	/**
	 * Lists all subsets of contained in this dataset
	 * @return
	 */
	public List<Dataset> listSubsets();
		
}
