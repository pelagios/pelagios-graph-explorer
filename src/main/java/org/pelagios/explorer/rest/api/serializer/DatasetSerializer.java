package org.pelagios.explorer.rest.api.serializer;

import java.lang.reflect.Type;

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

    public JsonElement serialize(Dataset dataset, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.add(Dataset.KEY_NAME, new JsonPrimitive(dataset.getName()));
        json.add(KEY_ROOT_DATASET, new JsonPrimitive(dataset.getRootDataset().getName()));
        json.add(KEY_SUBSETS, new JsonPrimitive(dataset.listSubsets().size()));
        json.add(Dataset.KEY_GEOANNOTATIONS, new JsonPrimitive(dataset.countGeoAnnotations(true)));
        json.add(Dataset.KEY_PLACES, new JsonPrimitive(dataset.listPlaces(true).size()));
        return json;
    }

}