package org.pelagios.explorer.rest.api.serializer;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class GeometrySerializer implements JsonSerializer<Geometry> {

	public JsonElement serialize(Geometry geom, Type typeOfSrc,
			JsonSerializationContext context) {

		JsonArray json = new JsonArray();
		for (Coordinate c : geom.getCoordinates()) {
			JsonArray pt = new JsonArray();
			pt.add(new JsonPrimitive(c.x));
			pt.add(new JsonPrimitive(c.y));
			json.add(pt);
		}
		return json;
	}

}
