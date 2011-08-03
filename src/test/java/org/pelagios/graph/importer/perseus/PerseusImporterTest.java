package org.pelagios.graph.importer.perseus;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;
import org.pelagios.graph.DummyGraph;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.PelagiosGraphBuilder;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.exceptions.DatasetNotFoundException;
import org.pelagios.graph.importer.perseus.PerseusImporter;
import org.pelagios.graph.nodes.Dataset;

public class PerseusImporterTest {

    /**
     * Path to the RDF file
     */
    private static final String RDF_FILE = "data/datasets/perseus-greco-roman.rdf";

    @Test
    public void testHierarchy() {
        // TODO implement hierarchy test
    }
        
    @Test
    public void testPerseusImport() throws DatasetExistsException, DatasetNotFoundException {
        // Just test for failure of success
        HashMap<String, File> perseusFiles = new HashMap<String, File>();
        perseusFiles.put("Perseus Greco-Roman", new File(RDF_FILE));

        PerseusImporter importer = new PerseusImporter(perseusFiles);
        importer.importData(new DummyGraph());
    }

}
