package org.pelagios.importer.perseus;

import java.io.File;

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.exception.DatasetExistsException;
import org.pelagios.importer.AbstractDatasetImporter;

public class PerseusDatasetImporter extends AbstractDatasetImporter {

	private DatasetBuilder rootNode = new DatasetBuilder("Perseus");
	
	public PerseusDatasetImporter(File rdf) {
		super(rdf);
	}

	@Override
	public void importData(PelagiosGraph graph) throws DatasetExistsException {
		// Start by creating the root node
		graph.addDataset(rootNode);		
	}

}
