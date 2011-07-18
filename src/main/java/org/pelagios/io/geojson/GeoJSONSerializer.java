package org.pelagios.io.geojson;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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

	public String toString(Geometry geometry) {
		return GEOJSON_TEMPLATE
			.replace("@type@",
				GeometryType.valueOf(geometry.getGeometryType().toUpperCase()).toString())
			.replace("@coords@", serializeGeometry(geometry));
	}
	
	public JsonElement toJSONObject(Geometry geometry) {
		// TODO there's probably better ways to do this...
		return new JsonParser().parse(toString(geometry));
	}
	
	private String serializeGeometry(Geometry g) {
		if (g instanceof Point) {
			Point p = (Point) g;
			return serializeCoordinate(p.getCoordinate());
		} else if (g instanceof LineString) {
			return serializeLineString((LineString) g);
		} else if (g instanceof Polygon) {
			Polygon p = (Polygon) g;
			return "[" + serializeLineString(p.getExteriorRing()) + "]"; 
		} else {
			// Encountered an unimplemented type
			// Log this and fall back to centroid
			log.warn("Can't serialize geometry properly - falling back to centroid " + g.toText());
			return serializeCoordinate(g.getCentroid().getCoordinate());
		}
	}
	
	private String serializeLineString(LineString l) {
		StringBuffer sb = new StringBuffer("[");
		for (Coordinate c : l.getCoordinates()) {
			sb.append(serializeCoordinate(c) + ", ");
		}
		return sb.toString().substring(0, sb.length() - 2) + "]";
	}
	
	private String serializeCoordinate(Coordinate c) {
		return "[" + c.x + ", " + c.y + "]";
	}
	
}
