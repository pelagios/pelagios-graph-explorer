package org.pelagios.explorer.rest.api.serializer;

import java.lang.reflect.Type;
import java.util.List;

import org.pelagios.graph.Path;
import org.pelagios.graph.PelagiosGraphNode;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.Place;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PathSerializer implements JsonSerializer<Path> {

	private static final String KEY_FROM = "from";
	private static final String KEY_TO = "to";
	private static final String KEY_VIA = "via";
	
	public JsonElement serialize(Path path, Type typeOfSrc,
			JsonSerializationContext context) {

		List<PelagiosGraphNode> nodes = path.getNodes();  
		
		JsonObject json = new JsonObject();
		json.add(KEY_FROM,
			new JsonPrimitive(((Place) nodes.get(0)).getURI().toString()));
		json.add(KEY_TO,
			new JsonPrimitive(((Place) nodes.get(nodes.size()-1)).getURI().toString()));

		JsonArray via = new JsonArray();
		for (int i=1; i<nodes.size() - 1; i++) {
			JsonObject hop = new JsonObject();
			hop.add(nodes.get(i).getType().toString(), 
				new JsonPrimitive(((Dataset) nodes.get(i)).getName()));
			via.add(hop);
		}
		json.add(KEY_VIA, via);
		
		return json;
	}

}
