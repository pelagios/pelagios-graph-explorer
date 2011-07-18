package org.pelagios.graph.importer.pleiades.locations;

import java.lang.reflect.Type;

import org.pelagios.io.geojson.GeoJSONParser;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class PleiadesGeometryDeserializer implements JsonDeserializer<PleiadesGeometry> {
	
	private static final String RELATION_KEY = "relation";

	public PleiadesGeometry deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		JsonObject obj = json.getAsJsonObject();
		PleiadesGeometry geometry = new PleiadesGeometry();
		
		geometry.setRelation(obj.get(RELATION_KEY).getAsString());
		
		GeoJSONParser parser = new GeoJSONParser();
		geometry.setGeometry(parser.parse(obj));
		
		return geometry;
	}

}
