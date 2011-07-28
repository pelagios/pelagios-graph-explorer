package org.pelagios.explorer.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.exceptions.DatasetNotFoundException;
import org.pelagios.graph.exceptions.PlaceNotFoundException;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

@Path("/annotations")
public class GeoAnnotationController extends AbstractController {

	/**
	 * Log message String constants
	 */
	private static final String LOG_GET_ANNOTATIONS = " Getting annotations ";
	
	@GET
	@Produces("application/json")
	@Path("/")
	public Response getAnnotations(@QueryParam("place") String place, 
			@QueryParam("dataset") String dataset) throws 
			DatasetNotFoundException, PlaceNotFoundException, 
			URISyntaxException {
		
		log.info(request.getRemoteAddr() + LOG_GET_ANNOTATIONS + place + _ + dataset);
		PelagiosGraph graph = PelagiosGraph.getDefaultDB();
		Dataset ds = graph.getDataset(dataset);
		Place p = graph.getPlace(new URI((place)));
		
		List<GeoAnnotation> allAnnotations = ds.listGeoAnnotations(true);
		List<GeoAnnotation> filteredByPlace = new ArrayList<GeoAnnotation>();
		for (GeoAnnotation a : allAnnotations) {
			if (a.listPlaces().contains(p))
				filteredByPlace.add(a);
		}
		
		return Response.ok(toJSON(filteredByPlace)).build();
	}
	
}
