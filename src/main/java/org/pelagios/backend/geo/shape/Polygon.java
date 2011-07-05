package org.pelagios.backend.geo.shape;

import com.vividsolutions.jts.algorithm.CentroidArea;
import com.vividsolutions.jts.geom.Coordinate;

public class Polygon implements Shape {

	private LineString outline;
	
	public Polygon(LineString outline) {
		this.outline = outline;
	}
	
	public LineString getOutline() {
		return outline;
	}

	public Coordinate getCentroid() {
		CentroidArea c = new CentroidArea();
		c.add(outline.getCoordinate().toArray(new Coordinate[outline.getCoordinate().size()]));
		return c.getCentroid();
	}

	public String toWKT() {
		StringBuffer sb = new StringBuffer("POLYGON ((");
		
		for (Coordinate p : outline.getCoordinate()) {
			sb.append(p.x + " " + p.y + " " + p.z + ", ");
		}
		
		sb.delete(sb.length() - 2, sb.length());
		sb.append("))");
		return sb.toString();
	}
	
}