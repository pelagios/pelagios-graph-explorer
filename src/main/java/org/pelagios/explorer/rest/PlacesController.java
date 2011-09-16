package org.pelagios.explorer.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.pelagios.explorer.rest.api.ReferencesTo;
import org.pelagios.explorer.rest.api.Overlap;
import org.pelagios.explorer.rest.api.serializer.DatasetSerializer;
import org.pelagios.explorer.rest.api.serializer.PathSerializer;
import org.pelagios.explorer.rest.api.serializer.PlaceSerializer;
import org.pelagios.graph.Path;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.PelagiosGraphUtils;
import org.pelagios.graph.exceptions.DatasetNotFoundException;
import org.pelagios.graph.exceptions.PlaceNotFoundException;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

/**
 * This controller provides basic query operations on the Places stored in the
 * PELAGIOS graph.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 *
 */
@javax.ws.rs.Path("/places")
public class PlacesController extends AbstractController {

    // Log message String constants
    private static final String LOG_INTERSECT = " Intersection ";
    private static final String LOG_SHORTESTPATH = " Shortest path ";
    private static final String LOG_REFERENCES_TO = " Occurences ";
    private static final String LOG_STRONGLY_RELATED = " Strongly related place ";

    /**
     * Place Search. This method returns places which labels start with the
     * specified query string. I.e. 'aeg' will return Aegae, Agyptium Mare, 
     * Aegyptus, etc.
     * @param q the prefix query
     * @return the list of Places (JSON-formatted, see {@link PlaceSerializer} for information)
     */
    @GET
    @Produces("application/json")
    @javax.ws.rs.Path("/search")
    public Response searchPlaces(@QueryParam("q") String q, @QueryParam("callback") String jsonpCallback) {
        PelagiosGraph graph = PelagiosGraph.getDefaultDB();
        List<Place> hits = graph.searchPlaces(q, 15);
        return Response.ok(toJSON(hits, jsonpCallback)).build();
    }

    /**
     * Place-wise intersection. This method lists the places that two data sets have in common.
     * @param set1 the first data set's name
     * @param set2 the second data set's name
     * @return a JSON-formatted data structure containing information about the
     * overlap (see {@link Overlap} and {@link PlaceSerializer} for information)
     * @throws DatasetNotFoundException if one (or both) of the data sets were not found in the graph
     */
    @GET
    @Produces("application/json")
    @javax.ws.rs.Path("/intersect")
    public Response getSharedPlaces(@QueryParam("set1") String set1, @QueryParam("set2") String set2, 
            @QueryParam("callback") String jsonpCallback) throws DatasetNotFoundException {

        log.info(request.getRemoteAddr() + LOG_INTERSECT + set1 + _ + set2);
        
        PelagiosGraph graph = PelagiosGraph.getDefaultDB();
        List<Dataset> datasets = new ArrayList<Dataset>();
        datasets.add(graph.getDataset(set1));
        datasets.add(graph.getDataset(set2));

        // Note the internal graph API actually supports intersections between
        // arbitrary numbers of datasets - but we'll limit it to 2 for the
        // external REST API
        List<Place> commonPlaces = graph.listCommonPlaces(datasets);
        Overlap o = new Overlap(set1, set2, commonPlaces, PelagiosGraphUtils.toConvexHull(commonPlaces));

        return Response.ok(toJSON(o, jsonpCallback)).build();
    }

    /**
     * Find shortest path. This method computes the shortest path between two
     * places in the graph.
     * @param from the starting place URI
     * @param to the destination place URI
     * @return the list of Paths (JSON-formatted, see {@link PathSerializer} for information)
     * @throws PlaceNotFoundException if one (or both) of the places were not found in the graph
     * @throws URISyntaxException if the from/to parameter(s) was/were no valid URI(s)
     */
    @GET
    @Produces("application/json")
    @javax.ws.rs.Path("shortestPaths")
    public Response getShortestPaths(@QueryParam("from") String from, @QueryParam("to") String to,
            @QueryParam("callback") String jsonpCallback) throws PlaceNotFoundException, URISyntaxException {

        log.info(request.getRemoteAddr() + LOG_SHORTESTPATH + from + _ + to);
        
        PelagiosGraph graph = PelagiosGraph.getDefaultDB();
        Place pFrom = graph.getPlace(new URI(from));
        Place pTo = graph.getPlace(new URI(to));
        
        Set<Path> shortestPaths = graph.findShortestPaths(pFrom, pTo);
        
        // Fold the paths to a maximum of 12
        // TODO check whether this has any effect at all (and eliminate if possible)
        int loopCount = 0;
        while ((shortestPaths.size() > 12) && (loopCount < 3)) {
            for (Path p : shortestPaths) {
                p.fold();
            }
            shortestPaths = new HashSet<Path>(shortestPaths);
            loopCount++;
        }

        return Response.ok(toJSON(shortestPaths, jsonpCallback)).build();
    }

    /**
     * List references to Place. This method lists all data sets that include references to
     * the specified Place  
     * @param place the place URI
     * @return the list of data sets containing references (JSON-formatted, see {@link DatasetSerializer}
     * for information)
     * @throws PlaceNotFoundException if the Place was not found in the graph
     * @throws URISyntaxException if the specified place URI was not a valid URI 
     */
    @GET
    @Produces("application/json")
    @javax.ws.rs.Path("referencesTo")
    public Response referencesTo(@QueryParam("place") String place, @QueryParam("callback") String jsonpCallback)
        throws PlaceNotFoundException, URISyntaxException {

        log.info(request.getRemoteAddr() + LOG_REFERENCES_TO + place);

        PelagiosGraph graph = PelagiosGraph.getDefaultDB();
        Place p = graph.getPlace(new URI(place));
        List<GeoAnnotation> annotations = graph.listReferencesTo(p);

        // Compile a table dataset<->no. of records
        HashMap<Dataset, Integer> referencesTo = new HashMap<Dataset, Integer>();
        for (GeoAnnotation r : annotations) {
            Dataset parent = r.getParentDataset();

            Integer count = referencesTo.get(parent);
            if (count == null) {
                count = new Integer(1);
            } else {
                count = Integer.valueOf(count.intValue() + 1);
            }

            referencesTo.put(parent, count);
        }

        // The fun part - collapse the table from bottom-level datasets
        // upwards to end up at a reasonable number of total sets
        // TODO move this functionality into the PelagiosGraphUtils
        referencesTo = collapse(referencesTo, 12);

        // Wrap the results for JSON serialization
        List<ReferencesTo> refJson = new ArrayList<ReferencesTo>();
        for (Dataset s : referencesTo.keySet()) {
            refJson.add(new ReferencesTo(p.getURI().toString(), s.getName(), s.listGeoAnnotations(true).size(),
                    PelagiosGraphUtils.toConvexHull(s.listPlaces(true)), s.getRootDataset().getName(), referencesTo.get(s)));
        }

        return Response.ok(toJSON(refJson, jsonpCallback)).build();
    }

    // TODO move this functionality into the PelagiosGraphUtils    
    private HashMap<Dataset, Integer> collapse(HashMap<Dataset, Integer> datasets, int limit) {
        if (datasets.size() <= limit)
            return datasets;

        HashMap<Dataset, Integer> collapsed = new HashMap<Dataset, Integer>();
        for (Dataset s : datasets.keySet()) {
            Dataset parent = s.getParentDataset();
            if (parent == null) {
                collapsed.put(s, datasets.get(s));
                limit++;
            } else {
                Integer count = collapsed.get(parent);
                if (count == null) {
                    count = datasets.get(s);
                } else {
                    count = Integer.valueOf(count.intValue() + datasets.get(s).intValue());
                }

                collapsed.put(parent, count);
            }
        }

        return collapse(collapsed, limit);
    }

    /**
     * Find strongly related Places. This method returns a list of places with a high
     * number of co-references.
     * @param place the place URI
     * @return the list of strongly related places (JSON-formatted, see {@link PlaceSerializer} for information)
     * @throws PlaceNotFoundException if the place was not found in the graph
     * @throws URISyntaxException if the specified place URI was not a valid URI
     */
    @GET
    @Produces("application/json")
    @javax.ws.rs.Path("stronglyRelated")
    public Response stronglyRelatedPlaces(@QueryParam("place") String place, @QueryParam("callback") String jsonpCallback)
        throws PlaceNotFoundException, URISyntaxException {

        log.info(request.getRemoteAddr() + LOG_STRONGLY_RELATED + place);

        PelagiosGraph graph = PelagiosGraph.getDefaultDB();
        Place p = graph.getPlace(new URI(place));

        List<Place> relatedPlaces = graph.findStronglyRelatedPlaces(p, 2);
        return Response.ok(toJSON(relatedPlaces, jsonpCallback)).build();
    }

}
