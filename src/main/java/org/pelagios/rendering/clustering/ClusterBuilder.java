package org.pelagios.rendering.clustering;

import java.util.ArrayList;
import java.util.List;

import org.pelagios.backend.graph.PlaceNode;

import com.vividsolutions.jts.geom.Coordinate;

public class ClusterBuilder {
	
	private List<PlaceNode> places;
	
	public ClusterBuilder(List<PlaceNode> places) {
		this.places = places;
	}
	
	public List<Cluster> build(int threshold) {
		List<Cluster> clusters = new ArrayList<Cluster>();
		
		for (PlaceNode p : places) {
			boolean added = false;
			for (Cluster c : clusters) {
				if (distance(p, c) <= threshold) {
					c.addPlace(p);
					added = true;
					break;
				}
			}
			
			if (!added) {
				Cluster newCluster = new Cluster();
				newCluster.addPlace(p);
				clusters.add(newCluster);
			}
		}
		
		return clusters;
	}

	private double distance(PlaceNode p, Cluster c) {
		Coordinate centroid = p.getGeoJSONGeometry()
			.getGeometry().getCentroid().getCoordinate();
		
		double dLon = centroid.x - c.getLon();
		double dLat = centroid.y - c.getLat();
		
		return Math.pow(dLon, 2) + Math.pow(dLat, 2);
	}
	
}
