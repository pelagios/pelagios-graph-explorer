package org.pelagios.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.pelagios.backend.Backend;
import org.pelagios.backend.graph.DatasetNode;
import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.PlaceNode;
import org.pelagios.backend.graph.exception.DatasetNotFoundException;
import org.pelagios.rest.api.Link;

import com.google.gson.JsonObject;
import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

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
	public Response listDatasets(@QueryParam("links") boolean links) {
		PelagiosGraph graph = Backend.getInstance();
		List<DatasetNode> datasets = graph.listTopLevelDatasets();
		
		if (links) {
			// Compute link information
			List<Link> l = new ArrayList<Link>();
			for (int i=0; i<datasets.size(); i++) {
				DatasetNode source = datasets.get(i);
				
				for (int j=i + 1; j<datasets.size(); j++) {
					DatasetNode target = datasets.get(j);
					l.add(new Link(
							source.getName(),
							target.getName(),
							graph.listSharedPlaces(Arrays.asList(source, target)).size()
					));
				}
			}
			
			JsonObject json = new JsonObject();
			json.add("datasets", toJSONTree(datasets));
			json.add("links", toJSONTree(l));
			return Response.ok(json.toString()).build();
		}
		
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
		
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		for (PlaceNode p : Backend.getInstance().getDataset(dataset).listPlaces(true)) {
			coordinates.addAll(
					Arrays.asList(p.getGeoJSONGeometry().getGeometry().getCoordinates()));
		}
		ConvexHull cv = new ConvexHull(coordinates.toArray(new Coordinate[coordinates.size()]),
				new GeometryFactory());
		
		return Response.ok(toJSON(cv.getConvexHull())).build();
	}

}
