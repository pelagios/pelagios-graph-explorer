package org.pelagios.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.pelagios.backend.Backend;
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
			System.out.println("Computing path from " + pFrom.getLabel() + " to " + pTo.getLabel());
			
			for (org.pelagios.backend.graph.Path p : graph.findShortestPath(pFrom, pTo)) {
				System.out.println("------------");
				for (Object o : p.getEntities()) {
					System.out.println(o);
				}
				System.out.println();
			}
			
			return Response.ok("").build();	
		} catch (URISyntaxException e) {
			return Response.notAcceptable(null).build();
		}
	}

}
