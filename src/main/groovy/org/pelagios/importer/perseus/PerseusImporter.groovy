package org.pelagios.importer.perseus

import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.builder.PelagiosGraphImpl;

/**
 * Importer for a collection of Perseus data sets.
 * 
 * @author Rainer Simon
 */
class PerseusImporter {
	
	public PerseusImporter(HashMap<String, File> dataFiles, PelagiosGraphImpl graph) {
		DatasetBuilder rootNode = new DatasetBuilder("Perseus")
		graph.addDataset(rootNode) 
		
		for (String name : dataFiles.keySet()) {
			PerseusDatafileImporter importer =
				new PerseusDatafileImporter(name, dataFiles.get(name), rootNode)
					
			importer.importData(graph)
		}
	}
	
}
