package org.pelagios.explorer.rest.api.serializer;

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
	
	private static final String KEY_PARENT_DATASET = "dataset";
	private static final String KEY_ROOT_DATASET = "rootDataset";
	
	public JsonElement serialize(GeoAnnotation annotation, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add(GeoAnnotation.KEY_URI, new JsonPrimitive(annotation.getDataURL().toString()));
		json.add(KEY_PARENT_DATASET, new JsonPrimitive(annotation.getParentDataset().getName()));
		json.add(KEY_ROOT_DATASET, new JsonPrimitive(annotation.getRootDataset().getName()));
		return json;
	}
  
}
