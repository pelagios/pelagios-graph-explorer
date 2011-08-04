package org.pelagios.graph.importer.gap;

import java.io.File;

import org.junit.Test;
import org.pelagios.graph.DummyGraph;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.importer.gap.GAPImporter;

public class GAPImporterTest {

    private static final String RDF_FILE = "data/datasets/GAPtriples.n3";

    private static final GAPImporter importer = new GAPImporter(new File(RDF_FILE));

    @Test
    public void testImport() throws DatasetExistsException {
        // Just test for failure of success
        importer.importData(new DummyGraph());
    }

}
