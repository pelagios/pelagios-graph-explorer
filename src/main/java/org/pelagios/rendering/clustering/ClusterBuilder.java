package org.pelagios.rendering.clustering;

import java.util.ArrayList;
import java.util.List;

import org.pelagios.backend.graph.PlaceNode;

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
		double dLon = p.getLon() - c.getLon();
		double dLat = p.getLat() - c.getLat();
		
		return Math.pow(dLon, 2) + Math.pow(dLat, 2);
	}
	
}
