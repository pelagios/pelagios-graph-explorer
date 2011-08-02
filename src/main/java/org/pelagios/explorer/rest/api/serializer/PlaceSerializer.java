package org.pelagios.explorer.rest.api.serializer;

import java.lang.reflect.Type;

import org.pelagios.graph.nodes.Place;
import org.pelagios.io.geojson.GeoJSONSerializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * JSON serializer implementation for classes implementing the Place interface.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class PlaceSerializer implements JsonSerializer<Place> {

    public JsonElement serialize(Place place, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.add(Place.KEY_LABEL, new JsonPrimitive(place.getLabel()));
        json.add(Place.KEY_URI, new JsonPrimitive(place.getURI().toString()));
        json.add(Place.KEY_GEOMETRY, new GeoJSONSerializer().toJSONObject(place.getGeometry()));
        return json;
    }

}
