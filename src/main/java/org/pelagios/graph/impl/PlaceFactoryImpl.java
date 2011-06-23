package org.pelagios.graph.impl;

import java.net.URI;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.Place;
import org.pelagios.graph.PlaceFactory;

/**
 * Implementation of the Place factory.
 * 
 * @author Rainer Simon
 */
public class PlaceFactoryImpl extends AbstractFactoryImpl implements PlaceFactory {

	public PlaceFactoryImpl(PelagiosGraph graph) {
		super(graph.getGraphDB(), graph.getPlaceIndex());
	}

	public Place createPlace(String label, double lon, double lat, URI uri) {
		Transaction tx = graphDb.beginTx();
		try {
			Node node = graphDb.createNode();
			Place place = new PlaceImpl(node);
			place.setLabel(label);
			place.setLon(lon);
			place.setLat(lat);
			place.setURI(uri);
			index.add(node, Place.KEY_URI, uri);
			tx.success();
			return place;
		} finally {
			tx.finish();
		}
	}

}
