package org.pelagios.graph.importer.pleiades.locations;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.pelagios.api.GeoJSONGeometry;
import org.pelagios.api.GeoJSONGeometry.Relation;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class GeometryDeserializer implements JsonDeserializer<GeoJSONGeometry> {

	private static final String RELATION = "relation";
	private static final String TYPE = "type";
	private static final String COORDINATES = "coordinates";
	
	private Logger log = Logger.getLogger(GeometryDeserializer.class);
	
	private GeometryFactory geomFactory = new GeometryFactory();
	
	public GeoJSONGeometry deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
			
		GeoJSONGeometry geoJson = new GeoJSONGeometry();
		JsonObject obj = json.getAsJsonObject(); 
		
		// Relation ('rough' or 'precise')
		JsonElement relation = obj.get(RELATION);
		geoJson.setRelation(Relation.valueOf(relation.getAsString().toUpperCase()));
		
		// Type ('Point', 'Polygon' or 'LineString')
		JsonElement type = obj.get(TYPE);
		geoJson.setType(GeoJSONGeometry.Type.valueOf(type.getAsString().toUpperCase()));

		// The geometry
		if (geoJson.getType() == GeoJSONGeometry.Type.POINT) {
			geoJson.setGeometry(deserializePoint(obj.get(COORDINATES)));
		} else if (geoJson.getType() == GeoJSONGeometry.Type.LINESTRING){
			geoJson.setGeometry(deserializeLineString(obj.get(COORDINATES)));
		} else if (geoJson.getType() == GeoJSONGeometry.Type.POLYGON) {
			geoJson.setGeometry(deserializePolygon(obj.get(COORDINATES)));
		} else {
			// Should never happen
			throw new IllegalArgumentException("Unsupported GeoJSON geometry type: "
					+ geoJson.getType().toString());
		}
		
		return geoJson;
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
		
		return geomFactory.createPoint(new Coordinate(lon, lat, alt));
	}
	
	private LineString deserializeLineString(JsonElement coordinates) {
		JsonArray coordList = coordinates.getAsJsonArray();
		
		if (coordList.size() < 2)
			throw new JsonParseException("Invalid linestring: " + coordinates.toString());
		
		Coordinate[] coords = new Coordinate[coordList.size()];
		for (int i=0; i<coordList.size(); i++) {
			coords[i] = deserializePoint(coordList.get(i)).getCoordinate();
		}
		
		return geomFactory.createLineString(coords);
	}
	
	private Polygon deserializePolygon(JsonElement coordinates) {
		JsonArray linestrings = coordinates.getAsJsonArray();
		
		if (linestrings.size() < 1)
			throw new JsonParseException("Invalid polygon: " + coordinates.toString());
		
		if (linestrings.size() > 1) 
			log.warn("Encountered polygon with multiple LineStrings - ignoring anything but outline!");
		
		
		LinearRing shell = geomFactory.
			createLinearRing(deserializeLineString(linestrings.get(0)).getCoordinates());
		
		return geomFactory.createPolygon(shell, new LinearRing[0]);
	}
	


}
