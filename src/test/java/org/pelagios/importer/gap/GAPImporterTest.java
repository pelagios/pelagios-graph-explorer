package org.pelagios.importer.gap;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.pelagios.graph.MockGraph;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.importer.Hierarchy;

public class GAPImporterTest {
	
	private static final String RDF_FILE = "src/test/resources/datasets/GAPtriples.n3";
	
	private static final GAPImporter importer = new GAPImporter(new File(RDF_FILE));
	
	@Test
	public void testHierarchy() {
		Hierarchy h = importer
			.getHierarchy("http://www.google.com/books?id=-C0BAAAAQAAJ&pg=PA247#bbox=691,722,772,752");
		Assert.assertTrue(h.getLevels().size() == 1);
		Assert.assertEquals("GAP:-C0BAAAAQAAJ", h.getLevels().get(0));
		
		h =	importer
			.getHierarchy("http://www.google.com/books?id=SwEHAAAAQAAJ&pg=PA174#bbox=994,2115,1091,2139");		
		Assert.assertTrue(h.getLevels().size() == 1);
		Assert.assertEquals("GAP:SwEHAAAAQAAJ", h.getLevels().get(0));	
	}
	
	@Test
	public void testImport() throws DatasetExistsException {		
		importer.importData(new MockGraph());
	}

}
