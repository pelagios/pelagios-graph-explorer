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

	private static final String KEY_START = "start";
	private static final String KEY_END = "end";
	private static final String KEY_VIA = "via";
	
	private static final String KEY_PLACE = "place";
	private static final String KEY_LABEL = "label";
	private static final String KEY_ANNOTATIONS = "annotations";
	
	private static final String KEY_DATASET = "dataset";
	private static final String KEY_DATASET_SIZE = "datasetSize";
	private static final String KEY_ROOT_DATASET = "rootDataset";
	
	public JsonElement serialize(Path path, Type typeOfSrc,
			JsonSerializationContext context) {

		List<PelagiosGraphNode> nodes = path.getNodes();
		JsonObject json = new JsonObject();
		
		try {
			// Start
			Place pStart = (Place) nodes.get(0);
			Dataset firstHop = (Dataset) nodes.get(1);
			JsonObject start = new JsonObject();
			start.add(KEY_PLACE, new JsonPrimitive(pStart.getURI().toString()));
			start.add(KEY_LABEL, new JsonPrimitive(pStart.getLabel()));
			start.add(KEY_ANNOTATIONS, new JsonPrimitive(firstHop.countReferences(pStart, true))); 
			json.add(KEY_START, start);
			
			// End
			Place pEnd = (Place) nodes.get(nodes.size()-1);
			Dataset lastHop = (Dataset) nodes.get(nodes.size() - 2);
			JsonObject end = new JsonObject();
			end.add(KEY_PLACE, new JsonPrimitive(pEnd.getURI().toString()));
			end.add(KEY_LABEL, new JsonPrimitive(pEnd.getLabel()));
			end.add(KEY_ANNOTATIONS, new JsonPrimitive(lastHop.countReferences(pEnd, true))); 
			json.add(KEY_END, end);
			
			// Hops
			JsonArray via = new JsonArray();
			for (int i=1; i<nodes.size() - 1; i++) {
				try {
					Dataset d = (Dataset) nodes.get(i);
					JsonObject hop = new JsonObject();
					hop.add(KEY_DATASET, new JsonPrimitive(d.getName()));
					hop.add(KEY_DATASET_SIZE, new JsonPrimitive(d.listGeoAnnotations(true).size()));
					hop.add(KEY_ROOT_DATASET, new JsonPrimitive(d.getRoot().getName()));
					via.add(hop);
				} catch (Throwable t) {
					
				}
			}
			json.add(KEY_VIA, via);
		} catch (Throwable t) {
			
		}
		return json;
	}

}
