package org.pelagios.graph.importer.gap;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.pelagios.graph.DummyGraph;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.importer.Hierarchy;
import org.pelagios.graph.importer.gap.GAPImporter;

public class GAPImporterTest {

    private static final String RDF_FILE = "data/datasets/GAPtriples.n3";

    private static final GAPImporter importer = new GAPImporter(new File(RDF_FILE));

    @Test
    public void testHierarchy() {
        Hierarchy h = importer
                .getHierarchy("http://www.google.com/books?id=-C0BAAAAQAAJ&pg=PA247#bbox=691,722,772,752");
        Assert.assertTrue(h.getLevels().size() == 2);
        Assert.assertEquals("GAP:-C0BAAAAQAAJ", h.getLevels().get(0));
        Assert.assertEquals("GAP:-C0BAAAAQAAJ:PA247", h.getLevels().get(1));
        
        h = importer.getHierarchy("http://www.google.com/books?id=SwEHAAAAQAAJ&pg=PA174#bbox=994,2115,1091,2139");
        Assert.assertTrue(h.getLevels().size() == 2);
        Assert.assertEquals("GAP:SwEHAAAAQAAJ", h.getLevels().get(0));
        Assert.assertEquals("GAP:SwEHAAAAQAAJ:PA174", h.getLevels().get(0));
    }

    @Test
    public void testImport() throws DatasetExistsException {
        // Just test for failure of success
        importer.importData(new DummyGraph());
    }

}
