package org.pelagios.explorer.rest.json;

import java.lang.reflect.Type;

import org.pelagios.graph.nodes.Dataset;

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
		json.add(Dataset.KEY_RECORDS, new JsonPrimitive(dataset.listRecords().size()));
		json.add(Dataset.KEY_PLACES, new JsonPrimitive(dataset.listPlaces(true).size()));
		return json;
	}
  
}