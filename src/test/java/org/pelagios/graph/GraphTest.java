package org.pelagios.graph;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.builder.PelagiosGraphBuilder;
import org.pelagios.graph.builder.PlaceBuilder;
import org.pelagios.graph.exception.DatasetExistsException;
import org.pelagios.graph.exception.DatasetNotFoundException;
import org.pelagios.graph.exception.PlaceExistsException;
import org.pelagios.graph.exception.PlaceNotFoundException;

public class GraphTest {
	
	private static final String DATA_DIR = "c:/neo4j-data";
	
	private void printDataset(Dataset dataset, int lvl) {
		StringBuffer indent = new StringBuffer(" -");
		for (int i=0; i<lvl; i++) {
			indent.append("-");
		}
		
		System.out.println(indent.toString() + dataset.getName());
		if (dataset.hasSubsets()) {
			for (Dataset subset : dataset.listSubsets()) {
				printDataset(subset, lvl + 1);
			}
		}		
	}
	
	@BeforeClass
	public static void buildTestGraph() 
		throws URISyntaxException, DatasetNotFoundException, DatasetExistsException {
		
		// Get the Pelagios graph instance
		PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(DATA_DIR);
		PelagiosGraph graph = graphBuilder.build();
		
		// Create a few datasets
		System.out.print("Creating sample datasets... ");
		DatasetBuilder nomisma = new DatasetBuilder("nomisma");
		DatasetBuilder gap = new DatasetBuilder("gap");
		DatasetBuilder gapSub1 = new DatasetBuilder("gap-subset1");
		DatasetBuilder gapSub2 = new DatasetBuilder("gap-subset2");
		DatasetBuilder gapSub2Sub1 = new DatasetBuilder("gap-sub-subset1");
		DatasetBuilder gapSub2Sub2 = new DatasetBuilder("gap-sub-subset2");
		DatasetBuilder gapSub3 = new DatasetBuilder("gap-subset3");
		
		graph.addDataset(nomisma);
		graph.addDataset(gap);
		graph.addDataset(gapSub1, gap);
		graph.addDataset(gapSub2, gap);
		graph.addDataset(gapSub3, gap);
		graph.addDataset(gapSub2Sub1, gapSub2);
		graph.addDataset(gapSub2Sub2, gapSub2);
		System.out.println("done.");
		
		// Create a place
		System.out.print("Creating sample place... ");
		PlaceBuilder corsica = new PlaceBuilder("Corsica", 5, 40, new URI("http://pleiades.stoa.org/places/991339"));
		try {
			graph.addPlaces(Arrays.asList(corsica));
			System.out.println("done.");
		} catch (PlaceExistsException e) {
			// We don't want this exception to happen
			Assert.assertTrue(false);
		}
		
		try {
			System.out.print("Trying to create a duplicate place... ");
			graph.addPlaces(Arrays.asList(corsica));
			
			// We're trying to add a duplicate - so this time
			// we expect an exception!
			Assert.assertTrue(false);
		} catch (PlaceExistsException e) {
			// We're expecting this
			System.out.println("rejected. Perfect!");
		}
		graph.shutdown();
	}
	
	@Test
	public void testDatasets() {
		// TODO don't just print out, verify with assertions!
		PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(DATA_DIR);
		PelagiosGraph graph = graphBuilder.build();
		
		System.out.println("Logging sample dataset graph:");
		for (Dataset dataset : graph.listTopLevelDatasets()) {
			printDataset(dataset, 0);
		}
		graph.shutdown();
	}
	
	@Test
	public void testPlaces() throws URISyntaxException, PlaceNotFoundException {
		// TODO don't just print out, verify with assertions!
		PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(DATA_DIR);
		PelagiosGraph graph = graphBuilder.build();
		
		// Query by ID
		Place corsica = graph.getPlace(new URI("http://pleiades.stoa.org/places/991339"));
		System.out.println("Query by URI: " + corsica.getLabel());
		
		// Query by listPlaces()
		for (Place p : graph.listPlaces()) {
			System.out.println("Query by listPlaces(): " + p.getLabel());
		}		
		graph.shutdown();
	}
	
	@AfterClass
	public static void shutdown() {
		PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(DATA_DIR);
		PelagiosGraph graph = graphBuilder.build();
		graph.shutdown();
	}

}
