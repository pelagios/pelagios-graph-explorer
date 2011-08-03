package org.pelagios.graph.importer.spqr;

import java.io.File;

import org.junit.Test;
import org.pelagios.graph.DummyGraph;

public class SPQRImporterTest {

    private static final String RDF_FILE = "data/datasets/spqr/downloads";
    
    @Test
    public void testSQPRImport() {
        // Just test for failure of success
        SPQRImporter importer = new SPQRImporter(new File(RDF_FILE));
        importer.importData(new DummyGraph());
    }

}
