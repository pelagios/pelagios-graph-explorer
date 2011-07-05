package org.pelagios.backend.graph.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.pelagios.backend.graph.DataRecord;
import org.pelagios.backend.graph.Dataset;
import org.pelagios.backend.graph.PelagiosRelationships;
import org.pelagios.backend.graph.Place;

public class DataRecordImpl extends AbstractNodeImpl implements DataRecord {

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

	/**
	 * Batch-adds a list of place references to this data record. If
	 * the list contains URIs to places which are NOT in the graph, the
	 * method will simply skip those. All skipped places are added to 
	 * a list. This list is returned as result of the method. 
	 * @param uris the place URIs
	 * @param placeIndex the graph's Lucene index for places
	 * @return the list of places which were skipped during import
	 */
	List<URI> addPlaces(List<URI> uris, Index<Node> placeIndex) {
		List<URI> skipped = new ArrayList<URI>();
		for (URI uri : uris) {
			IndexHits<Node> hits = placeIndex.get(Place.KEY_URI, uri);
			if (hits.size() == 0) {
				log.warn("Place " + uri.toString() + " not in graph - skipping this reference");
				skipped.add(uri);
			} else {
				Node placeNode = hits.getSingle();
				backingNode.createRelationshipTo(placeNode, PelagiosRelationships.REFERENCES);
			}
		}
		return skipped;
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
