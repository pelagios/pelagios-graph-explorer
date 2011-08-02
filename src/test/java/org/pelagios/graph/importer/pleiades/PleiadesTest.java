package org.pelagios.graph.importer.pleiades;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pelagios.graph.builder.PlaceBuilder;
import org.pelagios.graph.importer.pleiades.PleiadesImporter;
import org.pelagios.io.geojson.GeoJSONParser;
import org.pelagios.io.geojson.GeoJSONSerializer;

/**
 * A few tests for the Pleiades importer.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class PleiadesTest {

    private static PleiadesDumpFiles dumpFiles = null;
    
    private static final String PLEIADES_BASE_URL = "http://pleiades.stoa.org/places/";

    private static List<PlaceBuilder> places;

    private static HashMap<String, String> randomSamples = new HashMap<String, String>();

    static {
        try {
            dumpFiles = new PleiadesDumpFiles(new File("data"));
        } catch (FileNotFoundException e) {
            // We'll check for failed initialization in the tests
        }
        
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
    public void testDumpFileInitialization() {
        Assert.assertNotNull(dumpFiles);
        Assert.assertNotNull(dumpFiles.getLocationsCSV());
        Assert.assertNotNull(dumpFiles.getNamesCSV());
    }
    
    @Test
    public void testImport() throws IOException {
        PleiadesImporter importer = new PleiadesImporter();
        places = importer.importPleiadesDump(dumpFiles.getLocationsCSV(), dumpFiles.getNamesCSV());
        Assert.assertTrue(places.size() > 0);

        for (PlaceBuilder p : places) {
            Assert.assertTrue(p.getURI().toString().startsWith(PLEIADES_BASE_URL));
            Assert.assertNotNull(p.getLabel());
            Assert.assertTrue(p.getLabel().length() > 0);
            Assert.assertNotNull(p.getGeometry());
        }
        System.out.println("Pleiades import complete.");
    }

    @Test
    public void testJsonSerializer() {
        System.out.print("Testing JSON (de)serialization");

        GeoJSONSerializer serializer = new GeoJSONSerializer();
        GeoJSONParser parser = new GeoJSONParser();

        for (PlaceBuilder p : places) {
            String before = serializer.toString(p.getGeometry());
            String after = serializer.toString(parser.parse(before));
            Assert.assertEquals(before.toString(), after.toString());
        }
        System.out.println(" - ok.");
    }

}
