package org.pelagios.geo.shape;

import com.vividsolutions.jts.geom.Coordinate;

public interface Shape {
	
	public Coordinate getCentroid();
	
	public String toWKT();

}
