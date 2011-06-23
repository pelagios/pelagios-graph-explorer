package org.pelagios.graph.impl;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import org.pelagios.graph.Dataset;
import org.pelagios.graph.DatasetFactory;
import org.pelagios.graph.PelagiosGraph;

/**
 * Implementation of the Dataset factory.
 *  
 * @author Rainer Simon
 */
public class DatasetFactoryImpl extends AbstractFactoryImpl implements DatasetFactory {
    
	public DatasetFactoryImpl(PelagiosGraph graph) {
		super(graph.getGraphDB(), graph.getDatasetIndex());
	}

	public Dataset createDataset(String name) {
		Transaction tx = graphDb.beginTx();
		try {
			Node node = graphDb.createNode();
			Dataset dataset = new DatasetImpl(node);
			dataset.setName(name);
			index.add(node, Dataset.KEY_NAME, name);
			tx.success();
			return dataset;
		} finally {
			tx.finish();
		}
	}
 
}
