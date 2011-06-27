package org.pelagios.geo.shape;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A 3D Point
 * 
 * @author Rainer Simon
 */
public class Point implements Shape {

	private Coordinate c;

	public Point(double lon, double lat, double alt) {
		c = new Coordinate(lon, lat, alt);
	}
	
	public double getLon() {
		return c.x;
	}

	public double getLat() {
		return c.y;
	}

	public double getAlt() {
		return c.z;
	}
	
	public Coordinate getCentroid() {
		return c;
	}
	
	public String toWKT() {
		return "POINT (" + c.x + " " + c.y + " " + c.z +")";
	}
	
}
