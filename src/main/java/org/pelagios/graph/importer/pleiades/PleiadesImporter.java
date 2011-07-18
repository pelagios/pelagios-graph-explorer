package org.pelagios.graph.importer.pleiades;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pelagios.graph.builder.PlaceBuilder;
import org.pelagios.graph.importer.pleiades.locations.LocationParser;
import org.pelagios.graph.importer.pleiades.locations.LocationRecord;
import org.pelagios.graph.importer.pleiades.names.NameParser;
import org.pelagios.graph.importer.pleiades.names.NameRecord;

public class PleiadesImporter {

	private static final String PLEIADES_BASE_URI = "http://pleiades.stoa.org/places/";

	public List<PlaceBuilder> importPleiadesDump(File locationsCSV, File namesCSV)
		throws IOException {
		
		LocationParser lp = new LocationParser();
		HashMap<String, LocationRecord> locations = lp.parseLocationsCSV(locationsCSV);
		
		NameParser np = new NameParser();
		HashMap<String, NameRecord> names = np.parseNamesCSV(namesCSV);
		
		return mergeRecords(locations, names);
	}
	
	private List<PlaceBuilder> mergeRecords(
			HashMap<String, LocationRecord> locations,
			HashMap<String, NameRecord> names) {
		
		List<PlaceBuilder> records = new ArrayList<PlaceBuilder>();	
		
		for (LocationRecord lRec : locations.values()) {
			NameRecord nRec = names.get(lRec.getPid());
			if (nRec != null) {
				try {
					records.add(new PlaceBuilder(
							nRec.getNameTransliterated(), 
							new URI(PLEIADES_BASE_URI + nRec.getPid()),
							lRec.getGeometry()));
				} catch (URISyntaxException e) {
					// Should never happen
					throw new IllegalArgumentException(e);
				}
			}
		}
		
		return records;
	}
	
}
