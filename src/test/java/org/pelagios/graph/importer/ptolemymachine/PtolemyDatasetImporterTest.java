package org.pelagios.graph.importer.ptolemymachine;

import java.io.File;

import org.junit.Test;
import org.pelagios.graph.DummyGraph;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.importer.ptolemymachine.PtolemyDatasetImporter;

public class PtolemyDatasetImporterTest {

    /**
     * Path to the RDF file
     */
    private static final String RDF_FILE = "data/datasets/ptolemy-oac.rdf";

    @Test
    public void testHierarchy() {
        // TODO implement hierarchy test
    }
    
    @Test
    public void testPtolemyMachineImport() throws DatasetExistsException {
        // Just test for failure of success
        PtolemyDatasetImporter importer = new PtolemyDatasetImporter(new File(RDF_FILE));
        importer.importData(new DummyGraph());
    }

}
