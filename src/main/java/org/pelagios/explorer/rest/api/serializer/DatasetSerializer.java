package org.pelagios.explorer.rest.api.serializer;

import java.lang.reflect.Type;
import java.util.HashMap;

import org.pelagios.graph.nodes.Dataset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * JSON serializer implementation for classes implementing the Dataset interface.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 *
 */
public class DatasetSerializer implements JsonSerializer<Dataset> {

    private static final String KEY_ROOT_DATASET = "rootDataset";
    private static final String KEY_SUBSETS = "subsets";
    
    private static HashMap<String, Integer> qndGeoAnnotationsCache = new HashMap<String, Integer>();
    private static HashMap<String, Integer> qndPlacesCache = new HashMap<String, Integer>();

    public JsonElement serialize(Dataset dataset, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.add(Dataset.KEY_NAME, new JsonPrimitive(dataset.getName()));
        json.add(KEY_ROOT_DATASET, new JsonPrimitive(dataset.getRootDataset().getName()));
        json.add(KEY_SUBSETS, new JsonPrimitive(dataset.listSubsets().size()));
        json.add(Dataset.KEY_GEOANNOTATIONS, new JsonPrimitive(countGeoAnnotations(dataset)));
        json.add(Dataset.KEY_PLACES, new JsonPrimitive(countPlaces(dataset)));
        return json;
    }
    
    private int countGeoAnnotations(Dataset dataset) {
        Integer annotations = qndGeoAnnotationsCache.get(dataset.getName());
        
        if (annotations == null) {
            annotations = Integer.valueOf(dataset.countGeoAnnotations(true));
            qndGeoAnnotationsCache.put(dataset.getName(), annotations);
        }
        
        return annotations.intValue();
    }
    
    private int countPlaces(Dataset dataset) {        
        Integer places = qndPlacesCache.get(dataset.getName());
        
        if (places == null) {
            places = Integer.valueOf(dataset.listPlaces(true).size());
            qndPlacesCache.put(dataset.getName(), places);
        }
        
        return places.intValue();
    }

}