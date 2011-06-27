package org.pelagios.geo.shape;

import java.util.ArrayList;
import java.util.List;

/**
 * A LineString, consisting of a list of Points.
 * 
 * @author Rainer Simon
 */
public class LineString implements Shape {

	private List<Point> points = new ArrayList<Point>();
	
	public void addPoint(Point p) {
		points.add(p);
	}
	
	public List<Point> getPoints() {
		return points;
	}

	public Point getCentroid() {
		// TODO implement this
		return new Point(0, 0, 0);
	}

	public String toWKT() {
		StringBuffer sb = new StringBuffer("LINESTRING (");
		
		for (Point p : points) {
			sb.append(p.getLon() + " " + p.getLat() + " " + p.getAlt() + ", ");
		}
		
		sb.delete(sb.length() - 2, sb.length());
		sb.append(")");
		return sb.toString();
	}
}
