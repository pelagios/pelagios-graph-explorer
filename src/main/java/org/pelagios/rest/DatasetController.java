package org.pelagios.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.pelagios.Backend;
import org.pelagios.graph.Dataset;
import org.pelagios.graph.exception.DatasetNotFoundException;

/**
 * This controller exposes JSON serializations of the PELAGIOS dataset
 * descriptions (i.e. their metadata - not their contents!)
 *  
 * @author Rainer Simon
 */
@Path("/datasets")
public class DatasetController extends AbstractController {
	
	@GET
	@Produces("application/json")
	@Path("/{name}")
	public Response getDataset(@PathParam("name") String name) 
		throws DatasetNotFoundException {
		
		Dataset dataset = Backend.getInstance().getDataset(name);		
		return Response.ok(toJSON(dataset)).build();
	}

}
