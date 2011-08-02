package org.pelagios.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * A collection of utility methods for processing data in the PELAGIOS graph.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class PelagiosGraphUtils {

    /**
     * Takes a list of GeoAnnotations and compiles a list of the places
     * they reference. The places are unique within the list, i.e. every 
     * place is only contained once. In addition the places, the list also
     * includes information on how often they were referenced in the
     * annotations. For example, if a list contains the following GeoAnnotations
     * 
     * Annotation 1 -- REFERENCES --> Place A
     * Annotation 2 -- REFERENCES --> Place B
     * Annotation 3 -- REFERENCES --> Place A
     * Annotation 4 -- REFERENCES --> Place C
     * 
     * the returned list will have the following information
     * 
     * Place A - 2 references
     * Place B - 1 reference
     * Place C - 1 reference
     * 
     * @param annotations the annotations
     * @return the list of unique places and their reference count
     */
    public static List<Count<Place>> getUniquePlaces(List<GeoAnnotation> annotations) {
        HashMap<Place, Count<Place>> placesMap = new HashMap<Place, Count<Place>>();
        for (GeoAnnotation a : annotations) {
            Place p = a.getPlace();
            Count<Place> count = placesMap.get(p);
            if (count == null) {
                count = new Count<Place>(p);
            }
            count.increment();
            placesMap.put(p, count);
        }
        return new ArrayList<Count<Place>>(placesMap.values());
    }

    /**
     * Takes a list of GeoAnnotations and compiles a list of the datasets the
     * annotations belong to. The datasets are unique within the list. In addition
     * the datasets, the result list also maintains information about how many
     * of the annotations belong to the dataset. For convenience, the list
     * is sorted in descending order. For example, if a list contains
     * the following GeoAnnotations
     * 
     * Annotation 1 <-- GEOANNOTATION -- Dataset A
     * Annotation 2 <-- GEOANNOTATION -- Dataset B
     * Annotation 3 <-- GEOANNOTATION -- Dataset B
     * Annotation 4 <-- GEOANNOTATION -- Dataset C
     * 
     * the returned list will have the following information
     * 
     * Dataset B - 2 annotations
     * Dataset A - 1 annotations
     * Dataset C - 1 annotations
     * 
     * @param annotations the annotations
     * @return the list of datasets and their reference count (sorted in descending order)
     */
    public static List<Count<Dataset>> rankDatasets(List<GeoAnnotation> annotations) {
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

    /**
     * Takes a list of places and computes the convex hull polygon of their combined 
     * geographical boundaries.
     * @param places the places
     * @return the convex hull polygon
     */
    public static Geometry toConvexHull(List<Place> places) {
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        for (Place p : places) {
            coordinates.addAll(Arrays.asList(p.getGeometry().getCoordinates()));
        }

        ConvexHull cv = new ConvexHull(coordinates.toArray(new Coordinate[coordinates.size()]), new GeometryFactory());

        return cv.getConvexHull();
    }
    
    /**
     * A simple class that wraps an object with a 'counter' (for use in various utility methods).
     * @author Rainer Simon <rainer.simon@ait.ac.at>
     * @param <T> the class
     */
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
            return Integer.valueOf(other.count).compareTo(Integer.valueOf(count));
        }

    }

}
