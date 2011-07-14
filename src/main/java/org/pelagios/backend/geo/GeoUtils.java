package org.pelagios.backend.geo;

import java.util.ArrayList;
import java.util.List;

import org.pelagios.backend.graph.PlaceNode;

import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class GeoUtils {
	
	public static Geometry computeConvexHull(List<PlaceNode> places) {
		List<Coordinate> c = new ArrayList<Coordinate>();
		for (PlaceNode p : places) {
			c.add(new Coordinate(p.getLon(), p.getLat()));
		}
		
		ConvexHull cv = 
			new ConvexHull(c.toArray(new Coordinate[c.size()]), new GeometryFactory());
		
		return cv.getConvexHull();
	}
	
}
