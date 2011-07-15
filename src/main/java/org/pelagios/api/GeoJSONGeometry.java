package org.pelagios.api;

import org.apache.log4j.Logger;
import org.pelagios.pleiades.importer.locations.GeometryDeserializer;

import com.google.gson.JsonParser;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Class representation of the Pleiades 'Geometry' GeoJSON structure.
 * 
 * @author Rainer Simon
 */
public class GeoJSONGeometry {
	
	private static final String GEOJSON_TEMPLATE = 
		"{\"relation\": \"@relation@\", " +
		 "\"type\": \"@type@\", " +
		 "\"coordinates\": @coords@}";
	
	public enum Relation { ROUGH , PRECISE }
	
	public enum Type { 
		POINT { public String toString(){ return "Point"; } },
		LINESTRING { public String toString(){ return "LineString"; } },
		POLYGON { public String toString(){ return "Polygon"; } }
	}
	
	private Relation relation;
	
	private Type type;
	
	private Geometry geometry;
	
	private Logger log = Logger.getLogger(GeoJSONGeometry.class);
	
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
	
	public Relation getRelation() {
		return relation;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	// {"relation": "rough", "type": "Polygon", "coordinates": [[[-4.0, 37.0], [-4.0, 38.0], [-3.0, 38.0], [-3.0, 37.0], [-4.0, 37.0]]]}"
	@Override
	public String toString() {
		return GEOJSON_TEMPLATE
			.replace("@relation@", relation.toString().toLowerCase())
			.replace("@type@", type.toString())
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
	
	public boolean equals(Object arg) {
		if (!(arg instanceof GeoJSONGeometry))
				return false;
		
		GeoJSONGeometry other = (GeoJSONGeometry) arg;
		
		if (this.type != other.type)
			return false;
		
		if (this.relation != other.relation)
			return false;
		
		return this.geometry.toText().equals(other.geometry.toText());
	}
	
	public int hashCode() {
		return (relation.name() + geometry.toText()).hashCode();
	}
	
	// TODO revise the API. Find a better solution for JSON<->Object translations
	public static GeoJSONGeometry fromString(String json) {
		GeometryDeserializer ds = new GeometryDeserializer();
		return ds.deserialize(new JsonParser().parse(json), null, null);
	}
	
}
