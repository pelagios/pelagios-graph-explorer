package org.pelagios.rest;

import org.pelagios.graph.DataRecord;
import org.pelagios.graph.Dataset;
import org.pelagios.graph.Place;
import org.pelagios.rest.json.DataRecordSerializer;
import org.pelagios.rest.json.DatasetSerializer;
import org.pelagios.rest.json.PlaceSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	}
	
	protected String toJSON(Object object) {
		Gson gson = gsonBuilder.create();
		return gson.toJson(object);
	}

}
