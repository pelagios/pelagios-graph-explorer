package org.pelagios.graph;

import java.util.ArrayList;
import java.util.List;

import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

public class PelagiosGraphUtils {

	public static List<Count<Place>> 
		getUniquePlaces(List<GeoAnnotation> annotations) {
		
		List<Count<Place>> uniquePlaces = new ArrayList<Count<Place>>();
		/*
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
		*/
			
		return uniquePlaces;
	}
	
	
	public static List<Count<Dataset>> getTopDatasets(List<GeoAnnotation> annotations) {
		List<Count<Dataset>> topDatasets = new ArrayList<Count<Dataset>>();
		
		// TODO implement 
		
		return topDatasets;
	}
	
	public static List<Path> zoomOut(List<Path> paths) {
		return null; 
	}

	public class Count<T extends Object> {
		
		private T graphNode;
		
		private int count;
		
		private Count(T graphNode, int count) {
			this.graphNode = graphNode;
			this.count = count;
		}
		
		public T getGraphNode() {
			return graphNode;
		}
		
		public int getCount() {
			return count;
		}
		
	}
	
}
