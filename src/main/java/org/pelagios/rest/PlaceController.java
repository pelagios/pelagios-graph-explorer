package org.pelagios.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.pelagios.Backend;
import org.pelagios.graph.Place;
import org.pelagios.graph.exception.DatasetNotFoundException;

/**
 * This controller exposes JSON serializations of PELAGIOS place
 * descriptions
 *  
 * @author Rainer Simon
 */
@Path("/places")
public class PlaceController extends AbstractController {
	
	/**
	 * Returns the list of places referred to in the specified 
	 * data set.
	 * @param dataset the data set
	 * @return the places in the data set
	 * @throws DatasetNotFoundException if the data set was not found
	 * in the graph (HTTP 404)
	 */
	@GET
	@Produces("application/json")
	@Path("/{dataset}")
	public Response getPlaces(@PathParam("dataset") String dataset) 
		throws DatasetNotFoundException {
		
		List<Place> places = Backend.getInstance().getDataset(dataset).listPlaces();	
		return Response.ok(toJSON(places)).build();
	}

}
