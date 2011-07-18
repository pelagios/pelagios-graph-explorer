package org.pelagios.io.geojson;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class GeoJSONSerializer {
	
	private static final String GEOJSON_TEMPLATE = 
		"{\"type\": \"@type@\", " +
		 "\"coordinates\": @coords@}";
	
	private Logger log = Logger.getLogger(GeoJSONSerializer.class);

	public String serialize(Geometry geometry) {
		return GEOJSON_TEMPLATE
			.replace("@type@",
				GeometryType.valueOf(geometry.getGeometryType().toUpperCase()).toString())
			.replace("@coords@", toString(geometry));
	}
	
	private String toString(Geometry g) {
		if (g instanceof Point) {
			Point p = (Point) g;
			return toString(p.getCoordinate());
		} else if (g instanceof LineString) {
			return toString((LineString) g);
		} else if (g instanceof Polygon) {
			Polygon p = (Polygon) g;
			return "[" + toString(p.getExteriorRing()) + "]"; 
		} else {
			// Encountered an unimplemented type
			// Log this and fall back to centroid
			log.warn("Can't serialize geometry properly - falling back to centroid " + g.toText());
			return toString(g.getCentroid().getCoordinate());
		}
	}
	
	private String toString(LineString l) {
		StringBuffer sb = new StringBuffer("[");
		for (Coordinate c : l.getCoordinates()) {
			sb.append(toString(c) + ", ");
		}
		return sb.toString().substring(0, sb.length() - 2) + "]";
	}
	
	private String toString(Coordinate c) {
		return "[" + c.x + ", " + c.y + "]";
	}
	
}
