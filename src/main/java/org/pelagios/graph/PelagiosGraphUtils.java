package org.pelagios.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

public class PelagiosGraphUtils {

	public static List<Count<Place>> 
		getUniquePlaces(List<GeoAnnotation> annotations) {
		
		HashMap<Place, Count<Place>> placesMap = new HashMap<Place, Count<Place>>();
		for (GeoAnnotation a : annotations) {
			for (Place p : a.listPlaces()) {
				Count<Place> count = placesMap.get(p);
				if (count == null) {
					count = new Count<Place>(p);
				}
				count.increment();
				placesMap.put(p, count);
			}
		}
		return new ArrayList<Count<Place>>(placesMap.values());
	}
	
	public static List<Count<Dataset>> getTopDatasets(List<GeoAnnotation> annotations) {
		HashMap<Dataset, Count<Dataset>> datasetMap = new HashMap<Dataset, Count<Dataset>>();
		
		for (GeoAnnotation a : annotations) {
			Dataset parent = a.getParentDataset();
			Count<Dataset> count = datasetMap.get(parent);
			if (count == null) {
				count = new Count<Dataset>(parent);
			}
			count.increment();
			datasetMap.put(parent, count);
		}
		
		List<Count<Dataset>> topDatasets = new ArrayList<Count<Dataset>>(datasetMap.values());
		Collections.sort(topDatasets);		
		return topDatasets;
	}
	
	public static List<Path> zoomOut(List<Path> paths) {
		return null; 
	}

	public static class Count<T extends Object> implements Comparable<Count<T>> {
		
		private T elem;
		
		private int count = 0;
		
		public Count(T elem) {
			this.elem = elem;
		}
		
		public void increment() {
			count++;
		}
		
		public void add(int ct) {
			count += ct;
		}
		
		public T getElement() {
			return elem;
		}
		
		public int getCount() {
			return count;
		}

		public int compareTo(Count<T> other) {
			return Integer.valueOf(count).compareTo(Integer.valueOf(other.count));
		}
		
	}
	
}
