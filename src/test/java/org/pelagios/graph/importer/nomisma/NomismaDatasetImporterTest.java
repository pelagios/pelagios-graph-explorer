package org.pelagios.graph.importer.nomisma;

import java.io.File;

import org.junit.Test;
import org.pelagios.graph.DummyGraph;
import org.pelagios.graph.exceptions.DatasetExistsException;

public class NomismaDatasetImporterTest {

    private static final String RDF_FILE = "data/datasets/nomisma.org.rdf";

    @Test
    public void testNomismaImport() throws DatasetExistsException {
        // Just test for failure of success
        NomismaDatasetImporter importer = new NomismaDatasetImporter(new File(RDF_FILE));
        importer.importData(new DummyGraph());
    }

}
