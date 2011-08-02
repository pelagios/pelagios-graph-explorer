package org.pelagios.graph;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.builder.GeoAnnotationBuilder;
import org.pelagios.graph.builder.PlaceBuilder;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.exceptions.DatasetNotFoundException;
import org.pelagios.graph.exceptions.PlaceExistsException;
import org.pelagios.graph.exceptions.PlaceNotFoundException;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

/**
 * A dummy graph for importer tests.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class DummyGraph extends PelagiosGraph {

    @Override
    public List<Dataset> listTopLevelDatasets() {
        return null;
    }

    @Override
    public void addDataset(DatasetBuilder dataset) throws DatasetExistsException { }

    @Override
    public void addDataset(DatasetBuilder dataset, DatasetBuilder parent) 
        throws DatasetExistsException, DatasetNotFoundException { }

    @Override
    public Dataset getDataset(String name) throws DatasetNotFoundException {
        return null;
    }

    @Override
    public void addGeoAnnotations(List<GeoAnnotationBuilder> records, DatasetBuilder parent)
            throws DatasetNotFoundException { }

    @Override
    public void addPlaces(List<PlaceBuilder> places) throws PlaceExistsException { }

    @Override
    public Place getPlace(URI uri) throws PlaceNotFoundException {
        return null;
    }

    @Override
    public List<Place> searchPlaces(String prefix, int limit) {
        return null;
    }

    @Override
    public Iterable<Place> listPlaces() {
        return null;
    }

    @Override
    public List<Place> findStronglyRelatedPlaces(Place place, int limit) {
        return null;
    }

    @Override
    public List<GeoAnnotation> listReferencesTo(Place place) {
        return null;
    }

    @Override
    public List<Place> listCommonPlaces(List<Dataset> datasets) {
        return null;
    }

    @Override
    public Set<Path> findShortestPaths(Place from, Place to) {
        return null;
    }

    @Override
    public void shutdown() { }

}
