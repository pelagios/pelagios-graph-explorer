package org.pelagios.importer.nomisma;

import java.io.File;

import org.junit.Test;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.PelagiosGraphBuilder;
import org.pelagios.graph.exception.DatasetExistsException;

public class NomismaDatasetImporterTest {
	
	/**
	 * neo4j data directory
	 */
	private static final String DATA_DIR = "c:/neo4j-data";
	
	/**
	 * Path to the RDF file
	 */
	private static final String RDF_FILE = "src/test/resources/datasets/nomisma.org.pelagios.rdf";

	@Test
	public void testNomismaImport() throws DatasetExistsException {
		NomismaDatasetImporter importer = new NomismaDatasetImporter(new File(RDF_FILE));	
		
		PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(DATA_DIR);
		PelagiosGraph graph = graphBuilder.build();
		importer.importData(graph);
	}
	
}
