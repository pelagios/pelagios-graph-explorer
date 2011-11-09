package org.pelagios.explorer.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.pelagios.explorer.rest.api.serializer.DatasetSerializer;
import org.pelagios.explorer.rest.api.serializer.GeoAnnotationSerializer;
import org.pelagios.explorer.rest.api.serializer.PlaceSerializer;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.PelagiosGraphUtils;
import org.pelagios.graph.exceptions.DatasetNotFoundException;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This controller exposes information about PELAGIOS data sets stored in the graph
 * (metadata, referenced places, etc).
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
@Path("/datasets")
public class DatasetController extends AbstractController {

    /**
     * Log message String constants
     */
    private static final String LOG_TOP_LEVEL_LIST = " Listing top level datasets ";
    private static final String LOG_SUBSETS = " Listing subsets to ";

    /**
     * Returns a list of all top-level data sets stored in the Pelagios graph.
     * @return the data sets (see {@link DatasetSerializer} for information)
     */
    @GET
    @Produces("application/json")
    @Path("/")
    public Response listDatasets(@QueryParam("callback") String jsonpCallback) {
        log.info(request.getRemoteAddr() + LOG_TOP_LEVEL_LIST);
        
        PelagiosGraph graph = PelagiosGraph.getDefaultDB();
        List<Dataset> datasets = graph.listTopLevelDatasets();
        return Response.ok(toJSON(datasets, jsonpCallback)).build();
    }

    /**
     * Returns a list of all sub-sets to the specified parent data set.
     * @param superset the parent data set
     * @return the sub-sets the parent set's sub sets
     * @throws DatasetNotFoundException if the parent data set was not found in the graph
     */
    @GET
    @Produces("application/json")
    @Path("/{superset}")
    public Response getSubsets(@PathParam("superset") String superset, @QueryParam("callback") String jsonpCallback) 
        throws DatasetNotFoundException {
        
        log.info(request.getRemoteAddr() + LOG_SUBSETS + superset);
        
        List<Dataset> datasets = PelagiosGraph.getDefaultDB().getDataset(superset).listSubsets();
        return Response.ok(toJSON(datasets, jsonpCallback)).build();
    }

    /**
     * Returns a list of all places referenced in the specified data set (including its subsets).
     * @param dataset the data set
     * @return the places the JSON-formatted list of Places (see {@link PlaceSerializer} for information)
     * @throws DatasetNotFoundException if the specified data set is not in the graph
     */
    @GET
    @Produces("application/json")
    @Path("/{dataset}/places")
    public Response getPlaces(@PathParam("dataset") String dataset, @QueryParam("callback") String jsonpCallback)
        throws DatasetNotFoundException {
        
        List<Place> places = PelagiosGraph.getDefaultDB().getDataset(dataset).listPlaces(true);
        return Response.ok(toJSON(places, jsonpCallback)).build();
    }

    /**
     * Returns the convex hull of the places referenced in the specified data set (including its subsets).
     * @param dataset the data set
     * @return the convex hull
     * @throws DatasetNotFoundException if the specified data set is not in the graph
     */
    @GET
    @Produces("application/json")
    @Path("/{dataset}/places/convexhull")
    public Response getConvexHull(@PathParam("dataset") String dataset, @QueryParam("callback") String jsonpCallback)
        throws DatasetNotFoundException {
        
        PelagiosGraph graph = PelagiosGraph.getDefaultDB();
        Geometry cv = PelagiosGraphUtils.toConvexHull(graph.getDataset(dataset));
        return Response.ok(toJSON(cv, jsonpCallback)).build();
    }

    /**
     * Returns the GeoAnnotations contained in the specified data set (including its sub sets).
     * @param dataset the data set
     * @return the list of GeoAnnotations (see {@link GeoAnnotationSerializer} for information
     * @throws DatasetNotFoundException if the data set was not found in the graph
     */
    @GET
    @Produces("application/json")
    @Path("/{dataset}/annotations")
    public Response getGeoAnnotations(@PathParam("dataset") String dataset, @QueryParam("callback") String jsonpCallback)
        throws DatasetNotFoundException {
        
        List<GeoAnnotation> annotations = PelagiosGraph.getDefaultDB().getDataset(dataset).listGeoAnnotations(true);
        return Response.ok(toJSON(annotations, jsonpCallback)).build();
    }

}
