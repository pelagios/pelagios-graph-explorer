package org.pelagios.graph.importer.perseus;

import java.io.File;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.pelagios.graph.DummyGraph;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.exceptions.DatasetNotFoundException;
import org.pelagios.graph.importer.Hierarchy;
import org.pelagios.graph.importer.perseus.PerseusImporter;

public class PerseusImporterTest {

    private static final String RDF_FILE = "data/datasets/perseus-greco-roman.rdf";

    @Test
    public void testHierarchy() {
        PerseusDatafileImporter importer = new PerseusDatafileImporter(
                "Greco-Roman", new File(RDF_FILE), new DatasetBuilder("Perseus"));
        
        Hierarchy h = importer
            .getHierarchy("org.perseus:entityoccurrence:1867974:1:Perseus:text:1999.01.0084:book=12:chapter=36");
        
        Assert.assertTrue(h.getLevels().size() == 4);
        Assert.assertEquals("Greco-Roman", h.getLevels().get(0));
        Assert.assertEquals("Greco-Roman:1999.01.0084", h.getLevels().get(1));
        Assert.assertEquals("Greco-Roman:1999.01.0084:Book 12", h.getLevels().get(2));
        Assert.assertEquals("Greco-Roman:1999.01.0084:Book 12:Chapter 36", h.getLevels().get(3));
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
