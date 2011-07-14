package org.pelagios.rest;

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

import org.pelagios.backend.Backend;
import org.pelagios.backend.graph.DataRecord;
import org.pelagios.backend.graph.Dataset;
import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.Place;
import org.pelagios.backend.graph.exception.DatasetNotFoundException;
import org.pelagios.backend.graph.exception.PlaceNotFoundException;
import org.pelagios.rest.api.Occurence;

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
		PelagiosGraph graph = Backend.getInstance();
		List<Place> hits = graph.searchPlaces(q, 15);
		return Response.ok(toJSON(hits)).build();
	}
	
	/**
	 * The method returns the 'overlap' of the specified data sets
	 * in terms of referenced places, i.e. a list of places that
	 * are referenced in all data sets.
	 * @param sets the data sets
	 * @return the place-wise intersection i.e. the places referenced
	 * in all of the data sets
	 * @throws DatasetNotFoundException if (at least) one of the data sets
	 * is not in the graph 
	 */
	@GET
	@Produces("application/json")
	@Path("/intersect")
	public Response getSharedPlaces(@QueryParam("set") List<String> sets)
		throws DatasetNotFoundException {
		
		PelagiosGraph graph = Backend.getInstance();
		
		List<Dataset> datasets = new ArrayList<Dataset>();
		for (String s : sets) {
			datasets.add(graph.getDataset(s));
		}
		
		List<Place> sharedPlaces = graph.listSharedPlaces(datasets);	
		return Response.ok(toJSON(sharedPlaces)).build();
	}
	
	@GET
	@Produces("application/json")
	@Path("shortestpath")
	public Response getShortestPaths(@QueryParam("from") String from, @QueryParam("to") String to)
		throws PlaceNotFoundException {
		
		PelagiosGraph graph = Backend.getInstance();
		
		try {
			Place pFrom = graph.getPlace(new URI(from));
			Place pTo = graph.getPlace(new URI(to));
			
			int ctr = 0;
			Dataset highest = null;
			for (org.pelagios.backend.graph.Path p : graph.findShortestPath(pFrom, pTo)) {
				ctr++;
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
			
			int occFrom = highest.countReferences(pFrom, true);
			int occTo = highest.countReferences(pTo, true);

			System.out.println("RESULTS: " + pFrom.getLabel() + " to " + pTo.getLabel());
			System.out.println("Referenced " + occFrom + "/" + occTo + " in " + highest.getName());
			
			return Response.ok("").build();	
		} catch (URISyntaxException e) {
			return Response.notAcceptable(null).build();
		}
	}
	
	@GET
	@Produces("application/json")
	@Path("references")
	public Response listReferences(@QueryParam("place") String place) throws
		PlaceNotFoundException, URISyntaxException {
		
		// Get all references to this place from the graph
		PelagiosGraph graph = Backend.getInstance();
		Place p = graph.getPlace(new URI(place));
		List<DataRecord> records = graph.listReferencesTo(p);
		
		// Compile a table dataset<->no. of records
		HashMap<Dataset, Integer> occurences = new HashMap<Dataset, Integer>();
		for (DataRecord r : records) {		
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
			occJson.add(new Occurence(s.getName(), occurences.get(s)));
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
