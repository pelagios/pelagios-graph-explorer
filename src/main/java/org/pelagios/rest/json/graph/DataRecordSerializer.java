package org.pelagios.rest.json.graph;

import java.lang.reflect.Type;

import org.pelagios.backend.graph.AnnotationNode;

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
public class DataRecordSerializer implements JsonSerializer<AnnotationNode> {
	
	public JsonElement serialize(AnnotationNode record, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add(AnnotationNode.KEY_URI, new JsonPrimitive(record.getDataURL().toString()));
		return json;
	}
  
}
