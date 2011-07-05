package org.pelagios.importer.pleiades.locations;

import java.lang.reflect.Type;

import org.pelagios.backend.geo.shape.LineString;
import org.pelagios.backend.geo.shape.Point;
import org.pelagios.backend.geo.shape.Polygon;
import org.pelagios.importer.pleiades.locations.Geometry.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.vividsolutions.jts.geom.Coordinate;

public class GeometryDeserializer implements JsonDeserializer<Geometry> {

	private static final String RELATION = "relation";
	private static final String TYPE = "type";
	private static final String COORDINATES = "coordinates";
	
	private Logger log = LoggerFactory.getLogger(GeometryDeserializer.class);
	
	public Geometry deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
			
		Geometry geometry = new Geometry();
		JsonObject obj = json.getAsJsonObject(); 
		
		// Relation ('rough' or 'precise')
		JsonElement relation = obj.get(RELATION);
		geometry.setRelation(Relation.valueOf(relation.getAsString().toUpperCase()));
		
		// Type ('Point', 'Polygon' or 'LineString')
		JsonElement type = obj.get(TYPE);
		geometry.setType(org.pelagios.importer.pleiades.locations.Geometry.Type.valueOf(type.getAsString().toUpperCase()));

		// The geometry
		if (geometry.getType() == org.pelagios.importer.pleiades.locations.Geometry.Type.POINT) {
			geometry.setShape(deserializePoint(obj.get(COORDINATES)));
		} else if (geometry.getType() == org.pelagios.importer.pleiades.locations.Geometry.Type.POLYGON) {
			geometry.setShape(deserializePolygon(obj.get(COORDINATES)));
		} else {
			geometry.setShape(deserializeLineString(obj.get(COORDINATES)));
		}
		
		return geometry;
	}
	
	private Point deserializePoint(JsonElement coordinates) {
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
		
		return new Point(lon, lat, alt);
	}
	
	private Polygon deserializePolygon(JsonElement coordinates) {
		JsonArray linestrings = coordinates.getAsJsonArray();
		
		if (linestrings.size() < 1)
			throw new JsonParseException("Invalid polygon: " + coordinates.toString());
		
		if (linestrings.size() > 1) 
			log.warn("Encountered polygon with multiple LineStrings - ignoring anything but outline!");
		
		return new Polygon(deserializeLineString(linestrings.get(0)));
	}
	
	private LineString deserializeLineString(JsonElement coordinates) {
		JsonArray coordList = coordinates.getAsJsonArray();
		
		if (coordList.size() < 2)
			throw new JsonParseException("Invalid linestring: " + coordinates.toString());
		
		LineString ls = new LineString();
		for (int i=0; i<coordList.size(); i++) {
			Point p = deserializePoint(coordList.get(i));
			ls.addCoordinate(new Coordinate(p.getLon(), p.getLat(), p.getAlt()));
		}
		
		return ls;
	}

}
