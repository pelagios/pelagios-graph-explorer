package org.pelagios.graph;

import java.util.HashMap;
import java.util.List;

import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

public class PelagiosGraphUtils {

	public static HashMap<Place, Integer> 
		getUniquePlaces(List<GeoAnnotation> annotations) {
		
		HashMap<Place, Integer> uniquePlaces = new HashMap<Place, Integer>();
		for (GeoAnnotation a : annotations) {
			for (Place p : a.listPlaces()) {
				Integer ct = uniquePlaces.get(p);
				if (ct == null) {
					ct = Integer.valueOf(1);
				} else {
					ct = Integer.valueOf(ct.intValue() + 1);
				}
				uniquePlaces.put(p, ct);
			}
		}
			
		return uniquePlaces;
	}
	
	public static List<Path> zoomOut(List<Path> paths) {
		return null; 
	}
	
}
