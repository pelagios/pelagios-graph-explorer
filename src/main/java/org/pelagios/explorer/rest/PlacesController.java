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

import org.pelagios.explorer.Config;
import org.pelagios.explorer.rest.api.Occurences;
import org.pelagios.explorer.rest.api.Overlap;
import org.pelagios.graph.Path;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.exceptions.DatasetNotFoundException;
import org.pelagios.graph.exceptions.PlaceNotFoundException;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

/**
 * This controller provides basic query operation on
 * the places stored in the PELAGIOS graph.
 * @author Rainer Simon
 */
@javax.ws.rs.Path("/places")
public class PlacesController extends AbstractController {

	/**
	 * Log message String constants
	 */
	private static final String LOG_INTERSECT = " Intersection ";
	private static final String LOG_SHORTESTPATH = " Shortest path ";
	private static final String LOG_OCCURENCES = " Occurences ";
	private static final String LOG_STRONGLY_RELATED = " Strongly related place "; 
	
	@GET
	@Produces("application/json")
	@javax.ws.rs.Path("/search")
	public Response searchPlaces(@QueryParam("q") String q) {
		PelagiosGraph graph = PelagiosGraph.getDefaultDB();
		List<Place> hits = graph.searchPlaces(q, 15);
		return Response.ok(toJSON(hits)).build();
	}
	
	@GET
	@Produces("application/json")
	@javax.ws.rs.Path("/intersect")
	public Response getSharedPlaces(@QueryParam("set1") String set1, @QueryParam("set2") String set2)
		throws DatasetNotFoundException {
			
		log.info(request.getRemoteAddr() + LOG_INTERSECT + set1 + _ + set2);
		PelagiosGraph graph = PelagiosGraph.getDefaultDB();
		List<Dataset> datasets = new ArrayList<Dataset>();
		datasets.add(graph.getDataset(set1));
		datasets.add(graph.getDataset(set2));
		
		// Note the internal graph API actually supports intersections between
		// arbitrary numbers of datasets - but we'll limit it to 2 for the
		// external REST API
		List<Place> commonPlaces = graph.listSharedPlaces(datasets);
		Overlap o = new Overlap(
				set1, set2,
				commonPlaces,
				toConvexHull(commonPlaces));
		
		return Response.ok(toJSON(o)).build();
	}
	
	@GET
	@Produces("application/json")
	@javax.ws.rs.Path("shortestpaths")
	public Response getShortestPaths(@QueryParam("from") String from, @QueryParam("to") String to)
		throws PlaceNotFoundException, URISyntaxException {
		
		log.info(request.getRemoteAddr() + LOG_SHORTESTPATH + from + _ + to);
		PelagiosGraph graph = PelagiosGraph.getDefaultDB();
		Place pFrom = graph.getPlace(new URI(from));
		Place pTo = graph.getPlace(new URI(to));
		Set<Path> shortestPaths = graph.findShortestPaths(pFrom, pTo);
		
		// Fold the paths to a maximum of 12
		int loopCount = 0;
		while ((shortestPaths.size() > Config.getInstance().getMaxNumberOfShortestPaths()) && (loopCount < 3)) {	
			for (Path p : shortestPaths) {
				p.fold();
			}
			shortestPaths = new HashSet<Path>(shortestPaths);
			loopCount++;
		}

		return Response.ok(toJSON(shortestPaths)).build();	
	}
	
	@GET
	@Produces("application/json")
	@javax.ws.rs.Path("occurences")
	public Response listOccurences(@QueryParam("place") String place) throws
		PlaceNotFoundException, URISyntaxException {
		
		log.info(request.getRemoteAddr() + LOG_OCCURENCES + place);
		
		// Get all references to this place from the graph
		PelagiosGraph graph = PelagiosGraph.getDefaultDB();
		Place p = graph.getPlace(new URI(place));
		List<GeoAnnotation> records = graph.listReferencesTo(p);
		
		// Compile a table dataset<->no. of records
		HashMap<Dataset, Integer> occurences = new HashMap<Dataset, Integer>();
		for (GeoAnnotation r : records) {		
			Dataset parent = r.getParentDataset();
			
			Integer count = occurences.get(parent);
			if (count == null) {
				count = new Integer(1);
			} else {
				count = Integer.valueOf(count.intValue() + 1);
			}
			
			occurences.put(parent, count);
		}

		// The fun part - collapse the table from bottom-level datasets
		// upwards to end up at a reasonable number of total sets
		occurences = collapse(occurences, 12);
		
		// Wrap the results for JSON serialization
		List<Occurences> occJson = new ArrayList<Occurences>();
		for (Dataset s : occurences.keySet()) {
			occJson.add(new Occurences(
					p.getURI().toString(),
					s.getName(),
					s.listGeoAnnotations(true).size(),
					toConvexHull(s.listPlaces(true)),
					s.getRoot().getName(), 
					occurences.get(s))
			);
		}
		
		return Response.ok(toJSON(occJson)).build();	
	}
	
	private HashMap<Dataset, Integer> collapse(HashMap<Dataset, Integer> datasets, int limit) {
		if (datasets.size() <= limit)
			return datasets;
		
		HashMap<Dataset, Integer> collapsed = new HashMap<Dataset, Integer>();
		for (Dataset s : datasets.keySet()) {
			Dataset parent = s.getParent();
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

	@GET
	@Produces("application/json")
	@javax.ws.rs.Path("stronglyRelated")
	public Response findStronglyRelatedPlace(@QueryParam("place") String place) throws
		PlaceNotFoundException, URISyntaxException {
		
		log.info(request.getRemoteAddr() + LOG_STRONGLY_RELATED + place);

		PelagiosGraph graph = PelagiosGraph.getDefaultDB();
		Place p = graph.getPlace(new URI(place));

		List<Place> relatedPlaces = graph.findStronglyRelatedPlaces(p, 2);
		return Response.ok(toJSON(relatedPlaces)).build();	
	}

}
