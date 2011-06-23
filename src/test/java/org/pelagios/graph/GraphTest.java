package org.pelagios.graph;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pelagios.graph.impl.DatasetFactoryImpl;
import org.pelagios.graph.impl.PelagiosGraphFactoryImpl;
import org.pelagios.graph.impl.PlaceFactoryImpl;

public class GraphTest {
	
	private static final String DATA_DIR = "c:/neo4j-data";
	
	private void printDataset(Dataset dataset, int lvl) {
		StringBuffer indent = new StringBuffer();
		for (int i=0; i<lvl; i++) {
			indent.append("  ");
		}
		
		System.out.println(indent.toString() + dataset.getName());
		if (dataset.hasSubsets()) {
			for (Dataset subset : dataset.listSubsets()) {
				printDataset(subset, lvl + 1);
			}
		}		
	}
	
	@BeforeClass
	public static void buildTestGraph() throws URISyntaxException {
		// Get the Pelagios graph instance
		PelagiosGraphFactory graphFactory = new PelagiosGraphFactoryImpl();
		PelagiosGraph graph = graphFactory.init(DATA_DIR);
		
		// Create a few datasets
		DatasetFactory datasetFactory = new DatasetFactoryImpl(graph);
		Dataset nomisma = datasetFactory.createDataset("nomisma");
		Dataset gap = datasetFactory.createDataset("gap");
		Dataset gapSub1 = datasetFactory.createDataset("gap-subset1");
		Dataset gapSub2 = datasetFactory.createDataset("gap-subset2");
		Dataset gapSub2Sub1 = datasetFactory.createDataset("gap-sub-subset1");
		Dataset gapSub2Sub2 = datasetFactory.createDataset("gap-sub-subset2");
		Dataset gapSub3 = datasetFactory.createDataset("gap-subset3");
		
		graph.addDataset(nomisma);
		graph.addDataset(gap);
		gap.appendSubset(gapSub1);
		gap.appendSubset(gapSub2);
		gap.appendSubset(gapSub3);
		gapSub2.appendSubset(gapSub2Sub1);
		gapSub2.appendSubset(gapSub2Sub2);
		
		// Create a few places
		PlaceFactory placeFactory = new PlaceFactoryImpl(graph);
		Place corsica = placeFactory.createPlace("Corsica", 5, 40, new URI("http://pleiades.stoa.org/places/991339"));
		graph.addPlace(corsica);
		
		graph.shutdown();
	}
	
	@Test
	public void testDatasets() {
		// TODO don't just print out, verify with assertions!
		PelagiosGraphFactory graphFactory = new PelagiosGraphFactoryImpl();
		PelagiosGraph graph = graphFactory.init(DATA_DIR);
		for (Dataset dataset : graph.listTopLevelDatasets()) {
			printDataset(dataset, 0);
		}
		graph.shutdown();
	}
	
	@Test
	public void testPlaces() throws URISyntaxException {
		// TODO don't just print out, verify with assertions!
		PelagiosGraphFactory graphFactory = new PelagiosGraphFactoryImpl();
		PelagiosGraph graph = graphFactory.init(DATA_DIR);
		System.out.println(graph.getPlace(new URI("http://pleiades.stoa.org/places/991339")).getLabel());
		graph.shutdown();
	}
	
	@AfterClass
	public static void shutdown() {
		PelagiosGraphFactory graphFactory = new PelagiosGraphFactoryImpl();
		PelagiosGraph graph = graphFactory.init(DATA_DIR);
		graph.clear();
		graph.shutdown();
	}

}
