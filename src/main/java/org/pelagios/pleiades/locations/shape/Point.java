package org.pelagios.pleiades.locations.shape;


/**
 * A 3D Point
 * 
 * @author Rainer Simon
 */
public class Point implements Shape {

	private double lon, lat, alt;

	public Point(double lon, double lat, double alt) {
		this.lon = lon;
		this.lat = lat;
		this.alt = alt;
	}
	
	public double getLon() {
		return lon;
	}

	public double getLat() {
		return lat;
	}

	public double getAlt() {
		return alt;
	}

	public Point getCentroid() {
		return this;
	}
	
	@Override
	public String toString() {
		return "POINT: " + lon + ", " + lat + ", " + alt;
	}
	
}
