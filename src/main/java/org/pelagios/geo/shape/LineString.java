package org.pelagios.geo.shape;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.algorithm.CentroidLine;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * A LineString, consisting of a list of Points.
 * 
 * @author Rainer Simon
 */
public class LineString implements Shape {

	private List<Coordinate> coords = new ArrayList<Coordinate>();
	
	public void addCoordinate(Coordinate c) {
		coords.add(c);
	}
	
	public List<Coordinate> getCoordinate() {
		return coords;
	}

	public Coordinate getCentroid() {
		CentroidLine c = new CentroidLine();
		c.add(coords.toArray(new Coordinate[coords.size()]));
		return c.getCentroid();
	}

	public String toWKT() {
		StringBuffer sb = new StringBuffer("LINESTRING (");
		
		for (Coordinate c : coords) {
			sb.append(c.x + " " + c.y + " " + c.z + ", ");
		}
		
		sb.delete(sb.length() - 2, sb.length());
		sb.append(")");
		return sb.toString();
	}
}
