package org.pelagios.rest.json.geo;

import java.lang.reflect.Type;

import org.pelagios.api.GeoJSONGeometry;
import org.pelagios.backend.graph.PlaceNode;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GeoJSONSerializer implements JsonSerializer<GeoJSONGeometry> {

	public JsonElement serialize(GeoJSONGeometry geometry, Type typeOfSrc,
			JsonSerializationContext context) {

		JsonObject json = new JsonObject();
		json.add(PlaceNode.KEY_GEOMETRY, new JsonParser().parse(geometry.toString()));
		return json;
	}

}
