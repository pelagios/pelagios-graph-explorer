package org.pelagios.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

/**
 * This controller provides basic query operation on
 * the places stored in the PELAGIOS graph.
 * @author Rainer Simon
 */
@Path("/places")
public class PlacesController extends AbstractController {
	
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
		
		
		System.out.println(place);
		
		PelagiosGraph graph = Backend.getInstance();
		Place p = graph.getPlace(new URI(place));
		List<DataRecord> records = graph.listReferencesTo(p);
		System.out.println(records.size() + " references total");
		
		Collection<Dataset> uniqueDatasets = new HashSet<Dataset>();
		for (DataRecord r : records) {
			uniqueDatasets.add(r.getParentDataset());
		}
		uniqueDatasets = collapse(uniqueDatasets, 5);
		
		System.out.println("in " + uniqueDatasets.size() + " data sets");
		for (Dataset s : uniqueDatasets) {
			System.out.println(s.getName());
		}
		
		return Response.ok("").build();	
	}
	
	private Collection<Dataset> collapse(Collection<Dataset> datasets, int limit) {
		if (datasets.size() <= limit)
			return datasets;
		
		Set<Dataset> collapsed = new HashSet<Dataset>();
		for (Dataset s : datasets) {
			collapsed.add(s.getParent());
		}
		
		return collapse(collapsed, limit);
	}

}
