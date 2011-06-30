package org.pelagios.graph.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.pelagios.graph.DataRecord;
import org.pelagios.graph.Dataset;
import org.pelagios.graph.PelagiosRelationships;
import org.pelagios.graph.Place;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataRecordImpl extends AbstractNodeImpl implements DataRecord {
	
	Logger log = LoggerFactory.getLogger(AbstractNodeImpl.class);

	public DataRecordImpl(Node backingNode) {
		super(backingNode);
	}
	
	void setDataURL(URI url) {
		set(DataRecord.KEY_URI, url.toString());
	}

	public URI getDataURL() {
		try {
			return new URI(getAsString(DataRecord.KEY_URI));
		} catch (URISyntaxException e) {
			// Should never happen
			throw new RuntimeException(e);
		}
	}

	void addPlaces(List<URI> uris, Index<Node> placeIndex) {
		for (URI uri : uris) {
			IndexHits<Node> hits = placeIndex.get(Place.KEY_URI, uri);
			if (hits.size() == 0) {
				log.warn("Place " + uri.toString() + " not in graph - skipping this reference");
			} else {
				Node placeNode = hits.getSingle();
				backingNode.createRelationshipTo(placeNode, PelagiosRelationships.REFERENCES);
			}
		}
	}
	
	public List<Place> listPlaces() {
		List<Place> places = new ArrayList<Place>(); 
		for (Relationship r : backingNode.getRelationships(PelagiosRelationships.REFERENCES)) {
			places.add(new PlaceImpl(r.getEndNode()));
		}
		return places;
	}

	public Dataset getParentDataset() {
		Dataset d = null;
		for (Relationship r : backingNode.getRelationships(PelagiosRelationships.RECORD)) {
			// Can only have 1 relationship of type RECORD
			d = new DatasetImpl(r.getStartNode());
		}
		return d;
	}

	public Dataset getRootDataset() {
		// TODO Auto-generated method stub
		return null;
	}

}
