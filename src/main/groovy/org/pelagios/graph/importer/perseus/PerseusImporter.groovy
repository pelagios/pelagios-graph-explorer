package org.pelagios.graph.importer.perseus

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.DatasetBuilder;

/**
 * Importer for a collection of Perseus data sets.
 * 
 * @author Rainer Simon
 */
class PerseusImporter {
    
    private HashMap<String, File> dataFiles;
	
	public PerseusImporter(HashMap<String, File> dataFiles) {
        this.dataFiles = dataFiles
	}
    
    public void importData(PelagiosGraph graph) {
        DatasetBuilder rootNode = new DatasetBuilder("Perseus")
        graph.addDataset(rootNode)
        
        for (String name : dataFiles.keySet()) {
            PerseusDatafileImporter importer =
                new PerseusDatafileImporter(name, dataFiles.get(name), rootNode)
                    
            importer.importData(graph)
        }
    }
	
}
