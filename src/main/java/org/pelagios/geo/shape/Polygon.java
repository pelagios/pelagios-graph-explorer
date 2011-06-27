package org.pelagios.geo.shape;

public class Polygon implements Shape {

	private LineString outline;
	
	public Polygon(LineString outline) {
		this.outline = outline;
	}
	
	public LineString getOutline() {
		return outline;
	}

	public Point getCentroid() {
		// TODO implement this
		return new Point(0, 0, 0);
	}

	public String toWKT() {
		StringBuffer sb = new StringBuffer("POLYGON ((");
		
		for (Point p : outline.getPoints()) {
			sb.append(p.getLon() + " " + p.getLat() + " " + p.getAlt() + ", ");
		}
		
		sb.delete(sb.length() - 2, sb.length());
		sb.append("))");
		return sb.toString();
	}
	
}