package org.pelagios.graph;

import java.net.URI;
import java.util.List;

import org.pelagios.backend.graph.Dataset;
import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.Place;
import org.pelagios.backend.graph.builder.DataRecordBuilder;
import org.pelagios.backend.graph.builder.DatasetBuilder;
import org.pelagios.backend.graph.builder.PlaceBuilder;
import org.pelagios.backend.graph.exception.DatasetExistsException;
import org.pelagios.backend.graph.exception.DatasetNotFoundException;
import org.pelagios.backend.graph.exception.PlaceExistsException;

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

	public Place getPlace(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<Place> listPlaces() {
		// TODO Auto-generated method stub
		return null;
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public List<Place> listSharedPlaces(List<Dataset> datasets) {
		// TODO Auto-generated method stub
		return null;
	}

}
