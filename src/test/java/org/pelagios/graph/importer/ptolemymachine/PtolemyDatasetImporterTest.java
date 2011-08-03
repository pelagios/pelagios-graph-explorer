package org.pelagios.graph.importer.ptolemymachine;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.pelagios.graph.DummyGraph;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.importer.Hierarchy;
import org.pelagios.graph.importer.ptolemymachine.PtolemyDatasetImporter;

public class PtolemyDatasetImporterTest {

    private static final String RDF_FILE = "data/datasets/ptolemy.rdf";
    
    private static final PtolemyDatasetImporter importer = 
        new PtolemyDatasetImporter(new File(RDF_FILE));

    @Test
    public void testHierarchy() {
        Hierarchy h = importer
            .getHierarchy("urn:cts:greekLit:tlg0363.tlg009.chs01:4.7.10:???????????????");
    
        Assert.assertTrue(h.getLevels().size() == 2);
        Assert.assertEquals("Ptolemy Machine 4", h.getLevels().get(0));
        Assert.assertEquals("Ptolemy Machine 4:7", h.getLevels().get(1));
    }
    
    @Test
    public void testPtolemyMachineImport() throws DatasetExistsException {
        // Just test for failure of success
        importer.importData(new DummyGraph());
    }

}
