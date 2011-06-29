package org.pelagios.rest;

import java.util.List;

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
	@Path("/")
	public Response listDatasets() {
		List<Dataset> datasets = Backend.getInstance().listTopLevelDatasets();
		return Response.ok(toJSON(datasets)).build();
	}
	
	@GET
	@Produces("application/json")
	@Path("/{superset}")
	public Response getSubsets(@PathParam("superset") String superset) 
		throws DatasetNotFoundException {
		
		List<Dataset> datasets = Backend.getInstance().getDataset(superset).listSubsets();	
		return Response.ok(toJSON(datasets)).build();
	}

}
