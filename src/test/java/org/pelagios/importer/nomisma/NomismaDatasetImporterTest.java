package org.pelagios.importer.nomisma;

import java.io.File;

import org.junit.Test;
import org.pelagios.backend.graph.exception.DatasetExistsException;
import org.pelagios.graph.MockGraph;

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
