package org.pelagios.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.pelagios.Backend;
import org.pelagios.graph.Dataset;
import org.pelagios.graph.Place;
import org.pelagios.graph.exception.DatasetNotFoundException;

/**
 * This controller exposes JSON serializations of the PELAGIOS dataset
 * descriptions (i.e. their metadata - not their contents!)
 *  
 * @author Rainer Simon
 */
@Path("/datasets")
public class DatasetController extends AbstractController {

	/**
	 * Returns a list of all top-level data sets stored in the
	 * Pelagios graph.
	 * @return the data sets
	 */
	@GET
	@Produces("application/json")
	@Path("/")
	public Response listDatasets() {
		List<Dataset> datasets = Backend.getInstance().listTopLevelDatasets();
		return Response.ok(toJSON(datasets)).build();
	}
	
	/**
	 * Returns a list of all data sub-sets to the specified parent
	 * data set.
	 * @param superset the superset/parent data set
	 * @return the sub-sets
	 * @throws DatasetNotFoundException if the parent data set was not found (HTTP 404)
	 */
	@GET
	@Produces("application/json")
	@Path("/{superset}")
	public Response getSubsets(@PathParam("superset") String superset) 
		throws DatasetNotFoundException {
		
		List<Dataset> datasets = Backend.getInstance().getDataset(superset).listSubsets();	
		return Response.ok(toJSON(datasets)).build();
	}
	
	/**
	 * Returns a list of all places referenced in the specified data set 
	 * (including its subsets).
	 * @param dataset the data set
	 * @return the places
	 * @throws DatasetNotFoundException if the specified data set is not in the graph  
	 */
	@GET
	@Produces("application/json")
	@Path("/{dataset}/places")
	public Response getPlaces(@PathParam("dataset") String dataset)
		throws DatasetNotFoundException {
		
		List<Place> places = Backend.getInstance().getDataset(dataset).listPlaces(true);
		return Response.ok(toJSON(places)).build();
	}

}
