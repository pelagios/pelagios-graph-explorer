package org.pelagios.explorer.rest.api.serializer;

import java.lang.reflect.Type;

import org.pelagios.graph.nodes.GeoAnnotation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * JSON serializer implementation for classes implementing the GeoAnnotation interface.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class GeoAnnotationSerializer implements JsonSerializer<GeoAnnotation> {

    private static final String KEY_PARENT_DATASET = "dataset";
    private static final String KEY_ROOT_DATASET = "rootDataset";
    private static final String KEY_PROPERTIES = "properties";

    public JsonElement serialize(GeoAnnotation annotation, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.add(GeoAnnotation.KEY_URI, new JsonPrimitive(annotation.getTargetURI().toString()));
        json.add(GeoAnnotation.KEY_LABEL, new JsonPrimitive(annotation.getLabel()));
        json.add(KEY_PARENT_DATASET, new JsonPrimitive(annotation.getParentDataset().getName()));
        json.add(KEY_ROOT_DATASET, new JsonPrimitive(annotation.getParentDataset().getRootDataset().getName()));

        JsonObject properties = new JsonObject();
        for (String key : annotation.getPropertyKeys()) {
            properties.add(key, new JsonPrimitive(annotation.getProperty(key)));
        }
        json.add(KEY_PROPERTIES, properties);

        return json;
    }

}
