package org.pelagios.graph;

import java.net.URI;
import java.util.List;

import org.pelagios.backend.graph.AnnotationNode;
import org.pelagios.backend.graph.DatasetNode;
import org.pelagios.backend.graph.Path;
import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.PlaceNode;
import org.pelagios.backend.graph.builder.DataRecordBuilder;
import org.pelagios.backend.graph.builder.DatasetBuilder;
import org.pelagios.backend.graph.builder.PlaceBuilder;
import org.pelagios.backend.graph.exception.DatasetExistsException;
import org.pelagios.backend.graph.exception.DatasetNotFoundException;
import org.pelagios.backend.graph.exception.PlaceExistsException;
import org.pelagios.backend.graph.exception.PlaceNotFoundException;

public class MockGraph implements PelagiosGraph {

	public List<DatasetNode> listTopLevelDatasets() {
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

	public DatasetNode getDataset(String name) throws DatasetNotFoundException {
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

	public PlaceNode getPlace(URI uri) throws PlaceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<PlaceNode> searchPlaces(String prefix, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<PlaceNode> listPlaces() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<AnnotationNode> listReferencesTo(PlaceNode place)
			throws PlaceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<PlaceNode> listSharedPlaces(List<DatasetNode> datasets) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Path> findShortestPath(PlaceNode from, PlaceNode to)
			throws PlaceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
