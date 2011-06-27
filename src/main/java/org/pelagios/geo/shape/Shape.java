package org.pelagios.geo.shape;

public interface Shape {
	
	public Point getCentroid();
	
	public String toWKT();

}
