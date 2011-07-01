package org.pelagios.rest.json.graph;

import java.lang.reflect.Type;

import org.pelagios.graph.DataRecord;

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
public class DataRecordSerializer implements JsonSerializer<DataRecord> {
	
	public JsonElement serialize(DataRecord record, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add(DataRecord.KEY_URI, new JsonPrimitive(record.getDataURL().toString()));
		return json;
	}
  
}
