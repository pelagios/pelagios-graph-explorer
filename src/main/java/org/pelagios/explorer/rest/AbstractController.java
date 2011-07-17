package org.pelagios.explorer.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pelagios.explorer.rest.json.GeoAnnotationSerializer;
import org.pelagios.explorer.rest.json.DatasetSerializer;
import org.pelagios.explorer.rest.json.GeometrySerializer;
import org.pelagios.explorer.rest.json.PlaceSerializer;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class AbstractController {
	
	/**
	 * GSON serializer instance
	 */
	private GsonBuilder gsonBuilder;
	
	public AbstractController() {
		gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeHierarchyAdapter(Dataset.class, new DatasetSerializer());
		gsonBuilder.registerTypeHierarchyAdapter(GeoAnnotation.class, new GeoAnnotationSerializer());
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
	
	protected Geometry toConvexHull(List<Place> places) {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		for (Place p : places) {
			coordinates
				.addAll(Arrays.asList(p.getGeometry().getCoordinates()));
		}
		
		ConvexHull cv = new ConvexHull(
				coordinates.toArray(new Coordinate[coordinates.size()]),
				new GeometryFactory());
		
		return cv.getConvexHull();
	}

}
