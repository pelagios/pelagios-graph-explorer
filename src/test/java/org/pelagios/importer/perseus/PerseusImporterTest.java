package org.pelagios.importer.perseus;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;
import org.pelagios.backend.graph.Dataset;
import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.builder.PelagiosGraphBuilder;
import org.pelagios.backend.graph.exception.DatasetExistsException;
import org.pelagios.backend.graph.exception.DatasetNotFoundException;

public class PerseusImporterTest {
	
	private static final String NEO4J_DIR = "c:/neo4j-data";

	/**
	 * Path to the RDF file
	 */
	private static final String RDF_FILE = "src/test/resources/datasets/perseus-greco-roman.rdf";
	
	@Test
	public void testPerseusImport() throws DatasetExistsException, DatasetNotFoundException {
		PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(NEO4J_DIR);
		PelagiosGraph graph = graphBuilder.build();		
		
		HashMap<String, File> perseusFiles = new HashMap<String, File>();
		perseusFiles.put("Perseus Greco-Roman", new File(RDF_FILE));
		
		new PerseusImporter(perseusFiles, graph);
		
		Dataset perseus = graph.getDataset("Perseus");
		for (Dataset child : perseus.listSubsets()) {
			printDataset(child, 0);
		}
	}
	
	private void printDataset(Dataset dataset, int lvl) {
		StringBuffer indent = new StringBuffer(" -");
		for (int i=0; i<lvl; i++) {
			indent.append("-");
		}
		
		System.out.println(indent.toString() + dataset.getName());
		if (dataset.hasSubsets()) {
			for (Dataset subset : dataset.listSubsets()) {
				printDataset(subset, lvl + 1);
			}
		}		
	}
	
}
