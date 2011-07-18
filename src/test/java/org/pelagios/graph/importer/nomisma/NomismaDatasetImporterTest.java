package org.pelagios.graph.importer.nomisma;

import java.io.File;

import org.junit.Test;
import org.pelagios.graph.MockGraph;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.importer.nomisma.NomismaDatasetImporter;

public class NomismaDatasetImporterTest {
	
	/**
	 * Path to the RDF file
	 */
	private static final String RDF_FILE = "src/test/resources/datasets/nomisma.org.rdf";

	@Test
	public void testNomismaImport() throws DatasetExistsException {
		// Just test for failure of success
		NomismaDatasetImporter importer = new NomismaDatasetImporter(new File(RDF_FILE));	
		importer.importData(new MockGraph());
	}
	
}