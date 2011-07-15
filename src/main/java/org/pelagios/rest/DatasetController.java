package org.pelagios.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.pelagios.backend.Backend;
import org.pelagios.backend.graph.DatasetNode;
import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.PlaceNode;
import org.pelagios.backend.graph.exception.DatasetNotFoundException;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This controller exposes data about PELAGIOS data sets
 * stored in the graph (metadata, referenced places, etc).
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
		PelagiosGraph graph = Backend.getInstance();
		List<DatasetNode> datasets = graph.listTopLevelDatasets();
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
		
		List<DatasetNode> datasets = Backend.getInstance().getDataset(superset).listSubsets();	
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
		
		Geometry footprint = null;
		for (PlaceNode p : Backend.getInstance().getDataset(dataset).listPlaces(true)) {
			if (footprint == null) {
				footprint = p.getGeoJSONGeometry().getGeometry();
			} else {
				footprint.union(p.getGeoJSONGeometry().getGeometry());
			}
		}
		
		return Response.ok(toJSON(footprint)).build();
	}
	
	/**
	 * Returns the convex hull of the places referenced in the specified data set 
	 * (including its subsets).
	 * @param dataset the data set
	 * @return the convex hull
	 * @throws DatasetNotFoundException if the specified data set is not in the graph  
	 */
	@GET
	@Produces("application/json")
	@Path("/{dataset}/places/convexhull")
	public Response getConvexHull(@PathParam("dataset") String dataset)
		throws DatasetNotFoundException {
		
		PelagiosGraph graph = Backend.getInstance();
		Geometry cv = toConvexHull(graph.getDataset(dataset).listPlaces(true));
		
		return Response.ok(toJSON(cv)).build();
	}

}
