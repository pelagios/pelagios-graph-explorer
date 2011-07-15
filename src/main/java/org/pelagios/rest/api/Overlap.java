package org.pelagios.rest.api;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A simple class that wraps the information required for
 * drawing a link between two data set nodes in the GUI.
 * @author Rainer Simon
 *
 */
public class Overlap {
	
	/**
	 * The source dataset name
	 */
	String srcSet;
	
	/**
	 * The target dataset name
	 */
	String destSet;
	
	/**
	 * The number of places shared among the linked data sets
	 */
	int commonPlaces;
	
	/**
	 * The geographical area covered by the overlap
	 */
	Geometry footprint;
	
	public Overlap(String srcSet, String destSet, int commonPlaces, Geometry footprint) {
		this.srcSet = srcSet;
		this.destSet = destSet;
		this.commonPlaces = commonPlaces;
		this.footprint = footprint;
	}

}
