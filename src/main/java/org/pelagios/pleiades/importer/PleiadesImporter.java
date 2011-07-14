package org.pelagios.pleiades.importer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pelagios.api.Place;
import org.pelagios.pleiades.importer.locations.LocationParser;
import org.pelagios.pleiades.importer.locations.LocationRecord;
import org.pelagios.pleiades.importer.names.NameParser;
import org.pelagios.pleiades.importer.names.NameRecord;

public class PleiadesImporter {

	private static final String PLEIADES_BASE_URI = "http://pleiades.stoa.org/places/";

	public List<Place> importPleiadesDump(File locationsCSV, File namesCSV)
		throws IOException {
		
		LocationParser lp = new LocationParser();
		HashMap<String, LocationRecord> locations = lp.parseLocationsCSV(locationsCSV);
		
		NameParser np = new NameParser();
		HashMap<String, NameRecord> names = np.parseNamesCSV(namesCSV);
		
		return mergeRecords(locations, names);
	}
	
	private List<Place> mergeRecords(
			HashMap<String, LocationRecord> locations,
			HashMap<String, NameRecord> names) {
		
		List<Place> records = new ArrayList<Place>();	
		
		for (LocationRecord lRec : locations.values()) {
			NameRecord nRec = names.get(lRec.getPid());
			if (nRec != null) {
				try {
					records.add(new Place(
							nRec.getNameTransliterated(), 
							new URI(PLEIADES_BASE_URI + nRec.getPid()),
							lRec.getGeoJSON()));
				} catch (URISyntaxException e) {
					// Should never happen
					throw new IllegalArgumentException(e);
				}
			}
		}
		
		return records;
	}
	
}
