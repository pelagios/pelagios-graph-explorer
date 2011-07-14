package org.pelagios.rendering.clustering;

import java.util.ArrayList;
import java.util.List;

import org.pelagios.backend.graph.PlaceNode;

public class Cluster {

	private List<PlaceNode> places = new ArrayList<PlaceNode>();
	
	private double lon = 0;
	private double lat = 0;
	
	public void addPlace(PlaceNode place) {
		places.add(place);
		lon = (lon * (places.size() - 1) + place.getLon()) / places.size();
		lat = (lat * (places.size() - 1) + place.getLat()) / places.size();
	}
	
	public int size() {
		return places.size();
	}
	
	public double getLon() {
		return lon;
	}
	
	public double getLat() {
		return lat;
	}
	
	public List<PlaceNode> listPlaces() {
		return places;
	}
	
}
