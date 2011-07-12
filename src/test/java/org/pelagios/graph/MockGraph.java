package org.pelagios.graph;

import java.net.URI;
import java.util.List;

import org.pelagios.backend.graph.DataRecord;
import org.pelagios.backend.graph.Dataset;
import org.pelagios.backend.graph.Path;
import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.Place;
import org.pelagios.backend.graph.builder.DataRecordBuilder;
import org.pelagios.backend.graph.builder.DatasetBuilder;
import org.pelagios.backend.graph.builder.PlaceBuilder;
import org.pelagios.backend.graph.exception.DatasetExistsException;
import org.pelagios.backend.graph.exception.DatasetNotFoundException;
import org.pelagios.backend.graph.exception.PlaceExistsException;
import org.pelagios.backend.graph.exception.PlaceNotFoundException;

public class MockGraph implements PelagiosGraph {

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

	public void addDataRecords(List<DataRecordBuilder> records,
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

	public List<DataRecord> listReferencesTo(Place place)
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
