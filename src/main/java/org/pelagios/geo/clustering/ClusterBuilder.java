package org.pelagios.geo.clustering;

import java.util.ArrayList;
import java.util.List;

import org.pelagios.graph.Place;

public class ClusterBuilder {
	
	private List<Place> places;
	
	public ClusterBuilder(List<Place> places) {
		this.places = places;
	}
	
	public List<Cluster> build(int threshold) {
		List<Cluster> clusters = new ArrayList<Cluster>();
		
		for (Place p : places) {
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

	private double distance(Place p, Cluster c) {
		double dLon = p.getLon() - c.getLon();
		double dLat = p.getLat() - c.getLat();
		
		return Math.pow(dLon, 2) + Math.pow(dLat, 2);
	}
	
}
