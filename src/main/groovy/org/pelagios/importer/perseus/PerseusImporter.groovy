package org.pelagios.importer.perseus

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.DatasetBuilder;

/**
 * Importer for a collection of Perseus data sets.
 * 
 * @author Rainer Simon
 */
class PerseusImporter {
	
	public PerseusImporter(HashMap<String, File> dataFiles, PelagiosGraph graph) {
		DatasetBuilder rootNode = new DatasetBuilder("Perseus")
		graph.addDataset(rootNode) 
		
		for (String name : dataFiles.keySet()) {
			PerseusDatafileImporter importer =
				new PerseusDatafileImporter(name, dataFiles.get(name), rootNode)
					
			importer.importData(graph)
		}
	}
	
}
