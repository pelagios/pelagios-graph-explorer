package org.pelagios.importer.ptolemymachine;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.pelagios.graph.Dataset;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.PelagiosGraphBuilder;
import org.pelagios.graph.exception.DatasetExistsException;
import org.pelagios.graph.exception.PlaceNotFoundException;
import org.pelagios.importer.ptolemymachine.PtolemyDatasetImporter.Hierarchy;

public class PtolemyDatasetImporterTest {
	
	/**
	 * neo4j data directory
	 */
	private static final String DATA_DIR = "c:/neo4j-data";
	
	/**
	 * Path to the RDF file
	 */
	private static final String RDF_FILE = "src/test/resources/datasets/ptolemy-oac.rdf";
	
	/**
	 * A sampel URN to test hierarchy parsing
	 */
	private static final String SAMPLE_URN = "urn:cts:greekLit:tlg0363.tlg009.chs01:4.7.10:???????????????";
	
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
	
	@Test
	public void testImport() throws DatasetExistsException, PlaceNotFoundException {
		PtolemyDatasetImporter importer = new PtolemyDatasetImporter(new File(RDF_FILE));
		
		// Just a quick test on the hierarchy parsing method
		Hierarchy h = importer.getHierarchy(SAMPLE_URN);
		Assert.assertEquals(4, h.parentIdx);
		Assert.assertEquals(7, h.subsetIdx);
		
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
		for (Dataset dataset : graph.listTopLevelDatasets()) {
			printDataset(dataset, 0);
		}
		graph.shutdown();
	}

}
