package org.pelagios.explorer.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.pelagios.explorer.rest.api.serializer.DatasetSerializer;
import org.pelagios.explorer.rest.api.serializer.GeoAnnotationSerializer;
import org.pelagios.explorer.rest.api.serializer.GeometrySerializer;
import org.pelagios.explorer.rest.api.serializer.PathSerializer;
import org.pelagios.explorer.rest.api.serializer.PlaceSerializer;
import org.pelagios.graph.Path;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Base class with common functionality for all ReST controllers.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class AbstractController {

    /**
     * 'Space' String constant
     */
    protected static final String _ = " ";

    /**
     * JSON serializer instance
     */
    private GsonBuilder gsonBuilder;

    /**
     * Servlet context (injected by RESTEasy)
     */
    @Context
    protected HttpServletRequest request;

    /**
     * Logger
     */
    protected Logger log = Logger.getLogger(this.getClass());

    public AbstractController() {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Dataset.class, new DatasetSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(GeoAnnotation.class, new GeoAnnotationSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(Place.class, new PlaceSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(Geometry.class, new GeometrySerializer());
        gsonBuilder.registerTypeHierarchyAdapter(Path.class, new PathSerializer());
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
