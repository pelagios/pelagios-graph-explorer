package org.pelagios.graph;

import java.net.URI;
import java.util.List;

import org.pelagios.graph.builder.DataRecordBuilder;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.builder.PlaceBuilder;
import org.pelagios.graph.exception.DatasetExistsException;
import org.pelagios.graph.exception.DatasetNotFoundException;
import org.pelagios.graph.exception.PlaceExistsException;

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
