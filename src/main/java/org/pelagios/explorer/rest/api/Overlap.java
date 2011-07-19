package org.pelagios.explorer.rest.api;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This API domain object describes the overlap (in terms of
 * places, not area!) between two data sets. 
 * 
 * TODO the internal API allows computation of overlaps between
 * arbitrary numbers of data sets. We should also extend this
 * to this API domain object eventually!
 * 
 * @author Rainer Simon
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
