package org.pelagios.explorer.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.pelagios.explorer.Backend;
import org.pelagios.explorer.rest.api.CoReference;
import org.pelagios.explorer.rest.api.Occurence;
import org.pelagios.explorer.rest.api.Overlap;
import org.pelagios.graph.builder.PelagiosGraphImpl;
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
@Path("/places")
public class PlacesController extends AbstractController {
	
	@GET
	@Produces("application/json")
	@Path("/search")
	public Response searchPlaces(@QueryParam("q") String q) {
		PelagiosGraphImpl graph = Backend.getInstance();
		List<Place> hits = graph.searchPlaces(q, 15);
		return Response.ok(toJSON(hits)).build();
	}
	
	// TODO complete this methdo!
	@GET
	@Produces("application/json")
	@Path("/intersect/convexhull")
	public Response getSharedPlacesConvexHull(@QueryParam("set") List<String> sets)
		throws DatasetNotFoundException {
			
		PelagiosGraphImpl graph = Backend.getInstance();
		List<Dataset> datasets = new ArrayList<Dataset>();
		for (String s : sets) {
			datasets.add(graph.getDataset(s));
		}
		
		List<Place> commonPlaces = graph.listSharedPlaces(datasets);
		if (sets.size() == 2) {
			Overlap o = new Overlap(
					sets.get(0),
					sets.get(1),
					commonPlaces.size(),
					toConvexHull(commonPlaces));
			
			return Response.ok(toJSON(o)).build();
		} else {
			return Response.ok("").build();
		}
	}
	
	@GET
	@Produces("application/json")
	@Path("shortestpath")
	public Response getShortestPaths(@QueryParam("from") String from, @QueryParam("to") String to)
		throws PlaceNotFoundException, URISyntaxException {
		
		PelagiosGraphImpl graph = Backend.getInstance();
		Place pFrom = graph.getPlace(new URI(from));
		Place pTo = graph.getPlace(new URI(to));
		
		List<org.pelagios.graph.Path> shortestPaths = graph.findShortestPath(pFrom, pTo);

		Dataset highest = null;
		for (org.pelagios.graph.Path p : shortestPaths) {
			for (Object o : p.getEntities()) {
				if (o instanceof Dataset) {
					Dataset d = (Dataset) o;
					if (highest == null) {
						highest = d;
					} else if (highest.isSubsetOf(d)) {
						highest = d;
					}
				}
			}
		}
		
		List<CoReference> coreferencePaths = new ArrayList<CoReference>();
		CoReference coref = new CoReference(
				highest.getName(), 
				highest.countReferences(pFrom, true),
				highest.countReferences(pTo, true));
		coreferencePaths.add(coref);
		
		return Response.ok(toJSON(coreferencePaths)).build();	
	}
	
	@GET
	@Produces("application/json")
	@Path("references")
	public Response listReferences(@QueryParam("place") String place) throws
		PlaceNotFoundException, URISyntaxException {
		
		// Get all references to this place from the graph
		PelagiosGraphImpl graph = Backend.getInstance();
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
		List<Occurence> occJson = new ArrayList<Occurence>();
		for (Dataset s : occurences.keySet()) {
			occJson.add(new Occurence(s.getName(), s.getRoot().getName(), occurences.get(s)));
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

}
