package org.pelagios.pleiades;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pelagios.api.GeoJSONGeometry;
import org.pelagios.api.Place;
import org.pelagios.graph.importer.pleiades.PleiadesImporter;
import org.pelagios.graph.importer.pleiades.locations.GeometryDeserializer;

import com.google.gson.JsonParser;

public class PleiadesTest {
	
	private static File LOCATIONS_CSV = null;
	private static File NAMES_CSV = null;
	
	private static final String PLEIADES_BASE_URL = "http://pleiades.stoa.org/places/";
	
	private static List<Place> places;
	
	private static HashMap<String, String> randomSamples = new HashMap<String, String>();
	
	static {
		// Look for Pleiades dump files
		File resourcesDir = new File("src/test/resources");
		for (File f : resourcesDir.listFiles()) {
			if (f.getName().startsWith("pleiades-locations") &&
				f.getName().endsWith(".csv")) {
				
				LOCATIONS_CSV = f;
			} else if (f.getName().startsWith("pleiades-names") &&
				f.getName().endsWith(".csv")) {
				
				NAMES_CSV = f;
			}
		}
		
		if ((LOCATIONS_CSV == null) || (NAMES_CSV == null)) {
			throw new RuntimeException("Error: Pleiades dump files not found!");
		}
		
		// Init the index test samples
		randomSamples.put("http://pleiades.stoa.org/places/570484", "Methydrion");
		randomSamples.put("http://pleiades.stoa.org/places/207447", "Sirmium");
		randomSamples.put("http://pleiades.stoa.org/places/481785", "Ceraunii M.");
		randomSamples.put("http://pleiades.stoa.org/places/207495", "Tibiscum");
		randomSamples.put("http://pleiades.stoa.org/places/697622", "Abila");
		randomSamples.put("http://pleiades.stoa.org/places/413233", "Orcla");
		randomSamples.put("http://pleiades.stoa.org/places/687996", "Arethousa");
		randomSamples.put("http://pleiades.stoa.org/places/638753", "Aphrodeisias");
		randomSamples.put("http://pleiades.stoa.org/places/344448", "Lepcitani");
		randomSamples.put("http://pleiades.stoa.org/places/344282", "Sabratha");
		randomSamples.put("http://pleiades.stoa.org/places/344456", "Oeenses");
	}
	
	@Test
	public void testImport() throws IOException {
		PleiadesImporter importer = new PleiadesImporter();
		places = importer.importPleiadesDump(LOCATIONS_CSV, NAMES_CSV);
		Assert.assertTrue(places.size() > 0); 
		
		for (Place p : places) {
			Assert.assertTrue(p.getUri().toString().startsWith(PLEIADES_BASE_URL));
			Assert.assertNotNull(p.getLabel());
			Assert.assertTrue(p.getLabel().length() > 0);
			Assert.assertNotNull(p.getGeoJSON());
		}
		System.out.println("Pleiades import complete.");
	}
	
	@Test
	public void testJsonSerializer() {		
		System.out.print("Testing JSON (de)serialization");
		JsonParser parser = new JsonParser();
		GeometryDeserializer ds = new GeometryDeserializer();
		for (Place p : places) {
			GeoJSONGeometry before = p.getGeoJSON();
			GeoJSONGeometry after = ds.deserialize(
					parser.parse(before.toString()),
					null, null);
			Assert.assertEquals(before, after);
		}
		System.out.println(" - ok.");
	}

}
