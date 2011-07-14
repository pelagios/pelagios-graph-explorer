package org.pelagios.rest.json.graph;

import java.lang.reflect.Type;

import org.pelagios.backend.graph.DatasetNode;

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
public class DatasetSerializer implements JsonSerializer<DatasetNode> {
	
	public JsonElement serialize(DatasetNode dataset, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add(DatasetNode.KEY_NAME, new JsonPrimitive(dataset.getName()));
		json.add(DatasetNode.KEY_RECORDS, new JsonPrimitive(dataset.listRecords().size()));
		json.add(DatasetNode.KEY_PLACES, new JsonPrimitive(dataset.listPlaces(true).size()));
		return json;
	}
  
}