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
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("POLYGON: [");
		for (Point p : outline.getPoints()) {
			sb.append("[ " + p.toString() + " ], ");
		}
		return sb.substring(0, sb.length() - 2) + "]";
	}

	
}