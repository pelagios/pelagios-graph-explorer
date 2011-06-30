package org.pelagios.pleiades;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Test;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.Place;
import org.pelagios.graph.builder.PelagiosGraphBuilder;
import org.pelagios.graph.exception.PlaceExistsException;
import org.pelagios.graph.exception.PlaceNotFoundException;

public class PleiadesImporterTest extends TestCase {
	
	private static final String DATA_DIR = "c:/neo4j-data";

	// Note: you should always test with the latest Pleiades dump file
	private static final String LOCATIONS_CSV = "src/test/resources/pleiades-locations-20110627.csv";
	private static final String NAMES_CSV = "src/test/resources/pleiades-names-20110627.csv";
		
	@Test
	public void testPleiadesImporter()
		throws IOException, URISyntaxException, PlaceExistsException, PlaceNotFoundException {
		
		PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(DATA_DIR);
		PelagiosGraph graph = graphBuilder.build();
		
		PleiadesImporter importer = new PleiadesImporter();
		importer.importPleiadesDump(
				new File(LOCATIONS_CSV),
				new File(NAMES_CSV),
				graph);
		
		Place sample = graph.getPlace(new URI("http://pleiades.stoa.org/places/570481"));
		System.out.println(sample.getLabel() + " " + sample.getURI());
		assertTrue(sample.getLabel().equals("Anthana"));
	}
		
}
