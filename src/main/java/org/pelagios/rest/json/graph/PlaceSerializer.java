package org.pelagios.rest.json.graph;

import java.lang.reflect.Type;

import org.pelagios.backend.graph.PlaceNode;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * GSON serializer for classes implementing the Place interface.
 * 
 * @author Rainer Simon
 */
public class PlaceSerializer implements JsonSerializer<PlaceNode> {
	
	public JsonElement serialize(PlaceNode place, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add(PlaceNode.KEY_LABEL, new JsonPrimitive(place.getLabel()));
		json.add(PlaceNode.KEY_LON, new JsonPrimitive(place.getLon()));
		json.add(PlaceNode.KEY_LAT, new JsonPrimitive(place.getLat()));
		json.add(PlaceNode.KEY_URI, new JsonPrimitive(place.getURI().toString()));
		return json;
	}
  
}
