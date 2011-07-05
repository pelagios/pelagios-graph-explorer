package org.pelagios.rest;

import org.pelagios.backend.graph.DataRecord;
import org.pelagios.backend.graph.Dataset;
import org.pelagios.backend.graph.Place;
import org.pelagios.rest.json.geo.GeometrySerializer;
import org.pelagios.rest.json.graph.DataRecordSerializer;
import org.pelagios.rest.json.graph.DatasetSerializer;
import org.pelagios.rest.json.graph.PlaceSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.vividsolutions.jts.geom.Geometry;

public class AbstractController {
	
	/**
	 * GSON serializer instance
	 */
	private GsonBuilder gsonBuilder;
	
	public AbstractController() {
		gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeHierarchyAdapter(Dataset.class, new DatasetSerializer());
		gsonBuilder.registerTypeHierarchyAdapter(DataRecord.class, new DataRecordSerializer());
		gsonBuilder.registerTypeHierarchyAdapter(Place.class, new PlaceSerializer());
		gsonBuilder.registerTypeHierarchyAdapter(Geometry.class, new GeometrySerializer());
	}
	
	protected String toJSON(Object object) {
		Gson gson = gsonBuilder.create();
		return gson.toJson(object);
	}
	
	protected JsonElement toJSONTree(Object object) {
		Gson gson = gsonBuilder.create();
		return gson.toJsonTree(object);
	}

}
