package org.pelagios.rest;

import org.pelagios.api.GeoJSONGeometry;
import org.pelagios.backend.graph.AnnotationNode;
import org.pelagios.backend.graph.DatasetNode;
import org.pelagios.backend.graph.PlaceNode;
import org.pelagios.rest.json.geo.GeoJSONSerializer;
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
		gsonBuilder.registerTypeHierarchyAdapter(DatasetNode.class, new DatasetSerializer());
		gsonBuilder.registerTypeHierarchyAdapter(AnnotationNode.class, new DataRecordSerializer());
		gsonBuilder.registerTypeHierarchyAdapter(PlaceNode.class, new PlaceSerializer());
		gsonBuilder.registerTypeHierarchyAdapter(Geometry.class, new GeometrySerializer());
		gsonBuilder.registerTypeHierarchyAdapter(GeoJSONGeometry.class, new GeoJSONSerializer());
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
