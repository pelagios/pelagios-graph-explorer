package org.pelagios.graph.nodes;

import java.util.List;

/**
 * The Pelagios Dataset interface.
 * 
 * TODO we should return iterators instead of lists
 * for the listSubsets() and listPlaces() methods in
 * the future.
 * 
 * @author Rainer Simon
 */
public interface Dataset {
	
	/**
	 * Property key for the 'name' property
	 */
	public static final String KEY_NAME = "name";
	
	public static final String KEY_RECORDS = "records";
	
	public static final String KEY_PLACES = "places";
	
	/**
	 * Returns the name of this dataset.
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Returns the parent data set of this set, or
	 * <code>null</code> if it is a top-level data set.
	 * @return the parent or <code>null</code>
	 */
	public Dataset getParent();
	
	/**
	 * Returns the root data set of this set. Will return
	 * itself, in case this is a top-level data set.
	 * @return the root node of this data set
	 */
	public Dataset getRoot();
	
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
	 * Checks whether this data set is a sub set of the
	 * specified data set 
	 * @param d the possible parent data set
	 * @return true if this data set is a subset of d
	 */
	public boolean isSubsetOf(Dataset d);
	
	/**
	 * Returns a list of all data records contained in this data set.
	 * @return the list of data records;
	 */
	public List<GeoAnnotation> listRecords(boolean includeSubsets);
	

	/**
	 * Lists all places contained in this data set. The method
	 * will either return ONLY the places referenced in the
	 * records contained directly in this data set, or include
	 * also the places referenced in sub-sets of this data set,
	 * depending on the <code>includeSubsets</code> flag.  
	 * @param includeSubsets if <code>true</code>, places contained in
	 * sub-sets of this data set will also be returned
	 * @return the places
	 */
	public List<Place> listPlaces(boolean includeSubsets);
	
	/**
	 * Counts how often the specified place is referenced
	 * in this data set. If <code>includeSubsets</code> is set
	 * to true, the operation will also consider places contained
	 * in sub-sets of this data set.
	 * @param place the place
	 * @param includeSubsets if <code>true</code>, places contained in
	 * sub-sets of this data set will also be returned
	 * @return the number of times the place is referenced in the data set
	 */
	public int countReferences(Place place, boolean includeSubsets);
	
	/**
	 * Checks all places in the given list, and returns only those
	 * places from the list which are referenced in this data set.
	 * If <code>includeSubsets</code> is set to true, the operation
	 * will also consider places contained in sub-sets of this
	 * data set.
	 * @param places a list of places to check
	 * @param includeSubsets if <code>true</code>, places contained in
	 * sub-sets of this data set will also be returned
	 * @return those places from the list which are also referenced 
	 * in this data set
	 */
	public List<Place> filterReferenced(List<Place> places, boolean includeSubsets);
		
}
