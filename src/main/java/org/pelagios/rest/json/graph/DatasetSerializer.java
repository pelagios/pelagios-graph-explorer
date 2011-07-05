package org.pelagios.rest.json.graph;

import java.lang.reflect.Type;

import org.pelagios.backend.graph.Dataset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * GSON serializer for classes implementing the Dataset interface.
 * 
 * @author Rainer Simon
 */
public class DatasetSerializer implements JsonSerializer<Dataset> {
	
	public JsonElement serialize(Dataset dataset, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add(Dataset.KEY_NAME, new JsonPrimitive(dataset.getName()));
		return json;
	}
  
}