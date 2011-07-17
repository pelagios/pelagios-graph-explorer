package org.pelagios.explorer.rest.json;

import java.lang.reflect.Type;

import org.pelagios.graph.nodes.GeoAnnotation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * GSON serializer for classes implementing the DataRecord interface.
 * 
 * @author Rainer Simon
 */
public class GeoAnnotationSerializer implements JsonSerializer<GeoAnnotation> {
	
	public JsonElement serialize(GeoAnnotation record, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add(GeoAnnotation.KEY_URI, new JsonPrimitive(record.getDataURL().toString()));
		return json;
	}
  
}
