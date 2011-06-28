package org.pelagios.graph.builder;

import java.net.URI;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.pelagios.graph.DataRecord;
import org.pelagios.graph.Place;

public class DataRecordImpl extends AbstractNodeImpl implements DataRecord {

	public DataRecordImpl(Node backingNode) {
		super(backingNode);
	}
	
	void setDataURL(URI url) {
		set(DataRecord.KEY_URI, url);
	}

	public URI getDataURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Place> listPlaces() {
		// TODO Auto-generated method stub
		return null;
	}

}
