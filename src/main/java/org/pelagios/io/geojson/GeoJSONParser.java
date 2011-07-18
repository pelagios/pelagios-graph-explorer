package org.pelagios.io.geojson;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class GeoJSONParser {

	private static final String TYPE = "type";

	private static final String COORDINATES = "coordinates";
	
	private GeometryFactory geomFactory = new GeometryFactory();
	
	private Logger log = Logger.getLogger(GeoJSONParser.class);
	
	public Geometry parse(JsonObject json) {		
		GeometryType type = 
			GeometryType.valueOf(json.get(TYPE).getAsString().toUpperCase());

		if (type == GeometryType.POINT) {
			return parsePoint(json.get(COORDINATES));
		} else if (type == GeometryType.LINESTRING){
			return parseLineString(json.get(COORDINATES));
		} else if (type == GeometryType.POLYGON) {
			return parsePolygon(json.get(COORDINATES));
		}
		
		// Can never happen -> if type is neither Point, LineString nor 
		// Polygonm the valueOf() operation will throw an IllegalArgument
		// runtime exception
		return null;
	}
	
	private Point parsePoint(JsonElement coordinates) {
		JsonArray coords = coordinates.getAsJsonArray();
		
		if (coords.size() < 2 || coords.size() > 3) 
			throw new JsonParseException("Invalid point: " + coordinates);
		
		double lon = coords.get(0).getAsDouble();
		double lat = coords.get(1).getAsDouble();
		
		double alt;
		if (coords.size() > 2) {
			alt = coords.get(2).getAsDouble();
		} else {
			alt = 0;
		}
		
		return geomFactory.createPoint(new Coordinate(lon, lat, alt));
	}
	
	private LineString parseLineString(JsonElement coordinates) {
		JsonArray coordList = coordinates.getAsJsonArray();
		
		if (coordList.size() < 2)
			throw new JsonParseException("Invalid linestring: " + coordinates.toString());
		
		Coordinate[] coords = new Coordinate[coordList.size()];
		for (int i=0; i<coordList.size(); i++) {
			coords[i] = parsePoint(coordList.get(i)).getCoordinate();
		}
		
		return geomFactory.createLineString(coords);
	}
	
	private Polygon parsePolygon(JsonElement coordinates) {
		JsonArray linestrings = coordinates.getAsJsonArray();
		
		if (linestrings.size() < 1)
			throw new JsonParseException("Invalid polygon: " + coordinates.toString());
		
		if (linestrings.size() > 1) 
			log.warn("Encountered polygon with multiple LineStrings - ignoring anything but outline!");
		
		
		LinearRing shell = geomFactory.
			createLinearRing(parseLineString(linestrings.get(0)).getCoordinates());
		
		return geomFactory.createPolygon(shell, new LinearRing[0]);
	}
	


}
