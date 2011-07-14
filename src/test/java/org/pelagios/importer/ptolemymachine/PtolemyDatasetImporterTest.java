package org.pelagios.importer.ptolemymachine;

import java.io.File;

import org.junit.Test;
import org.pelagios.backend.graph.DatasetNode;
import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.builder.PelagiosGraphBuilder;
import org.pelagios.backend.graph.exception.DatasetExistsException;
import org.pelagios.backend.graph.exception.PlaceNotFoundException;
import org.pelagios.importer.ptolemymachine.PtolemyDatasetImporter;

public class PtolemyDatasetImporterTest {
	
	/**
	 * neo4j data directory
	 */
	private static final String DATA_DIR = "c:/neo4j-data";
	
	/**
	 * Path to the RDF file
	 */
	private static final String RDF_FILE = "src/test/resources/datasets/ptolemy-oac.rdf";
		
	private void printDataset(DatasetNode dataset, int lvl) {
		StringBuffer indent = new StringBuffer(" -");
		for (int i=0; i<lvl; i++) {
			indent.append("-");
		}
		
		System.out.println(indent.toString() + dataset.getName());
		if (dataset.hasSubsets()) {
			for (DatasetNode subset : dataset.listSubsets()) {
				printDataset(subset, lvl + 1);
			}
		}		
	}
	
	@Test
	public void testImport() throws DatasetExistsException, PlaceNotFoundException {
		PtolemyDatasetImporter importer = new PtolemyDatasetImporter(new File(RDF_FILE));
		
		// Run the import
		PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(DATA_DIR);
		PelagiosGraph graph = graphBuilder.build();
		importer.importData(graph);
		
		graph.shutdown();
	}
	
	@Test
	public void testDatasets() {
		// TODO don't just print out, verify with assertions!
		PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(DATA_DIR);
		PelagiosGraph graph = graphBuilder.build();
		System.out.println("Logging sample dataset graph:");
		for (DatasetNode dataset : graph.listTopLevelDatasets()) {
			printDataset(dataset, 0);
		}
		graph.shutdown();
	}

}
