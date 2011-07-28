package org.pelagios.graph;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.builder.PlaceBuilder;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.exceptions.DatasetNotFoundException;
import org.pelagios.graph.exceptions.PlaceExistsException;
import org.pelagios.graph.exceptions.PlaceNotFoundException;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.Place;
import org.pelagios.io.geojson.GeoJSONParser;
import org.pelagios.test.Setup;

public class PelagiosGraphTest {

	@Test
	public void testCreateGraph() {
		PelagiosGraph graph = Setup.getTestGraph();
		System.out.println("Test graph created.");
		Assert.assertTrue(graph.listTopLevelDatasets().size() == 0);
		Assert.assertFalse(graph.listPlaces().iterator().hasNext());
	}
	
	@Test
	public void testAddDatasets() throws DatasetExistsException, DatasetNotFoundException {
		System.out.print("Creating sample datasets... ");

		DatasetBuilder nomisma = new DatasetBuilder("nomisma");
		DatasetBuilder gap = new DatasetBuilder("gap");
		DatasetBuilder gapSub1 = new DatasetBuilder("gap-subset1");
		DatasetBuilder gapSub2 = new DatasetBuilder("gap-subset2");
		DatasetBuilder gapSub2Sub1 = new DatasetBuilder("gap-sub-subset1");
		DatasetBuilder gapSub2Sub2 = new DatasetBuilder("gap-sub-subset2");
		DatasetBuilder gapSub3 = new DatasetBuilder("gap-subset3");
		
		PelagiosGraph graph = Setup.getTestGraph();
		graph.addDataset(nomisma);
		graph.addDataset(gap);
		graph.addDataset(gapSub1, gap);
		graph.addDataset(gapSub2, gap);
		graph.addDataset(gapSub3, gap);
		graph.addDataset(gapSub2Sub1, gapSub2);
		graph.addDataset(gapSub2Sub2, gapSub2);
		
		List<Dataset> topLevelDatasets = graph.listTopLevelDatasets();
		Assert.assertTrue(topLevelDatasets.size() == 2);
				
		// Adding an existing dataset must throw a DatasetExistsException
		try {
			graph.addDataset(nomisma);
			Assert.assertTrue(false);
		} catch (DatasetExistsException e) {
			// Expected!
		}
		
		// Check correct child relations for "gap" test dataset
		Dataset dataset = null;
		for (Dataset d : topLevelDatasets) {
			if (d.getName().equals("gap"))
				dataset = d;
		}
		Assert.assertNotNull(dataset);
		Assert.assertTrue(dataset.hasSubsets());

		// Perform various tests on the graph structure
		List<Dataset> subsets = dataset.listSubsets();
		boolean subset1Found = false;
		boolean subset2Found = false;
		boolean subset3Found = false;
		Assert.assertTrue(subsets.size() == 3);
		for (Dataset subset : subsets) {
			// Test correct parent/child relations
			Assert.assertTrue(subset.getParent().getName().equals("gap"));
			Assert.assertTrue(subset.isSubsetOf(dataset));
			
			String n = subset.getName();
			if (n.equals("gap-subset2")) {
				subset2Found = true;
				Assert.assertTrue(subset.listSubsets().size() == 2);
			} else {
				Assert.assertFalse(subset.hasSubsets());
				Assert.assertTrue(subset.listSubsets().size() == 0);
				
				if (n.equals("gap-subset1")) {
					subset1Found = true;
				} else if (n.equals("gap-subset3")) {
					subset3Found = true;
				}
			}
		}
		Assert.assertTrue(subset1Found && subset2Found && subset3Found);

		System.out.println("done.");
	}
	
	@Test
	public void testAddPlace() throws URISyntaxException, PlaceExistsException, PlaceNotFoundException {
		System.out.print("Creating sample place... ");

		PlaceBuilder corsica = new PlaceBuilder(
				"Corsica", new URI("http://pleiades.stoa.org/places/991339"),
				new GeoJSONParser().parse("{ \"type\": \"Point\", \"coordinates\":[26.108246000000001,35.208534999999998] }"));

		PelagiosGraph graph = Setup.getTestGraph();		
		graph.addPlaces(Arrays.asList(corsica));
		
		System.out.println("done.");

		// Trying to create a duplicate places must throw a PlaceExistsException
		System.out.print("Trying to create a duplicate place... ");
		try {
			graph.addPlaces(Arrays.asList(corsica));
			Assert.assertTrue(false);
		} catch (PlaceExistsException e) {
			System.out.println("rejected. Perfect!");
		}
		
		System.out.print("Retrieving sample place... ");
		
		// Search via index
		Place place = graph.getPlace(new URI("http://pleiades.stoa.org/places/991339"));
		Assert.assertNotNull(place);
		Assert.assertEquals("Corsica", place.getLabel());
		
		// Search via listPlaces()
		for (Place p : graph.listPlaces()) {
			Assert.assertEquals("Corsica", p.getLabel());
		}		
		
		System.out.println("done.");
	}
	
}
