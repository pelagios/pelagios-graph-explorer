package org.pelagios.geo.clustering;

import java.util.ArrayList;
import java.util.List;

import org.pelagios.graph.Place;

public class Cluster {

	private List<Place> places = new ArrayList<Place>();
	
	private double lon = 0;
	private double lat = 0;
	
	public void addPlace(Place place) {
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
	
	public List<Place> listPlaces() {
		return places;
	}
	
}
