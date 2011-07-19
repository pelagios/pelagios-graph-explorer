package org.pelagios.explorer.rest.api;

/**
 * This API domain object represents 'occurences' of a Place in
 * a particular data set, i.e. how many GeoAnnotations in the
 * data set reference the Place.
 *  
 * @author Rainer Simon
 */
public class Occurences {
	
	/**
	 * The place URI
	 */
	String place;
	
	/**
	 * The data set
	 */
	String dataset;
	
	/**
	 * The total number of GeoAnnotations in the data set
	 */
	int datasetSize;
	
	/**
	 * The root data set of the data set, 
	 * e.g. 'Ptolemy Machine' for 'Ptolemy Machine 5:14'
	 */
	String rootDataset;
	
	/**
	 * The number of occurences in the data set, i.e.
	 * GeoAnnotations that reference the place 
	 */
	int occurences;
	
	public Occurences(String place, String dataset, int datasetSize, String rootDataset, int occurences) {
		this.place = place;
		this.dataset = dataset;
		this.datasetSize = datasetSize;
		this.rootDataset = rootDataset;
		this.occurences = occurences;
	}

}
