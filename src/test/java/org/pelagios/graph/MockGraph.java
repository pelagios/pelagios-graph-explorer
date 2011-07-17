package org.pelagios.graph;

import java.net.URI;
import java.util.List;

import org.pelagios.graph.builder.GeoAnnotationBuilder;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.builder.PelagiosGraphImpl;
import org.pelagios.graph.builder.PlaceBuilder;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.exceptions.DatasetNotFoundException;
import org.pelagios.graph.exceptions.PlaceExistsException;
import org.pelagios.graph.exceptions.PlaceNotFoundException;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.GeoAnnotation;
import org.pelagios.graph.nodes.Place;

public class MockGraph implements PelagiosGraphImpl {

	public List<Dataset> listTopLevelDatasets() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addDataset(DatasetBuilder dataset)
			throws DatasetExistsException {
		// TODO Auto-generated method stub
		
	}

	public void addDataset(DatasetBuilder dataset, DatasetBuilder parent)
			throws DatasetExistsException, DatasetNotFoundException {
		// TODO Auto-generated method stub
		
	}

	public Dataset getDataset(String name) throws DatasetNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public void addDataRecords(List<GeoAnnotationBuilder> records,
			DatasetBuilder parent) throws DatasetNotFoundException {
		// TODO Auto-generated method stub
		
	}

	public void addPlaces(List<PlaceBuilder> places)
			throws PlaceExistsException {
		// TODO Auto-generated method stub
		
	}

	public Place getPlace(URI uri) throws PlaceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Place> searchPlaces(String prefix, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<Place> listPlaces() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<GeoAnnotation> listReferencesTo(Place place)
			throws PlaceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Place> listSharedPlaces(List<Dataset> datasets) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Path> findShortestPath(Place from, Place to)
			throws PlaceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
