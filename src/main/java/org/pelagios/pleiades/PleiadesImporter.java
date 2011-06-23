package org.pelagios.pleiades;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.pleiades.locations.LocationParser;
import org.pelagios.pleiades.locations.LocationRecord;
import org.pelagios.pleiades.names.NameParser;
import org.pelagios.pleiades.names.NameRecord;

public class PleiadesImporter {

	private static final String PLEIADES_BASE_URI = "http://pleiades.stoa.org/places/";

	/**
	 * Imports a Pleiades dump directly into the Pelagios graph.
	 * @param locationsCSV the locations dump file
	 * @param namesCSV the names dump file
	 * @param graph the Pelagios graph
	 * @throws IOException if something is wrong with the dump files
	 */
	public void importPleiadesDump(File locationsCSV, File namesCSV, PelagiosGraph graph) throws IOException {
		LocationParser lp = new LocationParser();
		HashMap<String, LocationRecord> locations = lp.parseLocationsCSV(locationsCSV);
		
		NameParser np = new NameParser();
		HashMap<String, NameRecord> names = np.parseNamesCSV(namesCSV);
		
		List<MergedRecord> records = mergeRecords(locations, names);
		
		// TODO add to graph
	}
	
	private List<MergedRecord> mergeRecords(
			HashMap<String, LocationRecord> locations,
			HashMap<String, NameRecord> names) {
		
		List<MergedRecord> records = new ArrayList<MergedRecord>();	
		
		for (LocationRecord lRec : locations.values()) {
			NameRecord nRec = names.get(lRec.getPid());
			if (nRec != null) {
				records.add(new MergedRecord(lRec, nRec));
			}
		}
		
		return records;
	}
	
	/**
	 * A simple class that wraps a Pleiades location record with its
	 * corresponding name record.
	 */
	class MergedRecord {
		
		LocationRecord lRec;
		
		NameRecord nRec;
		
		MergedRecord(LocationRecord lRec, NameRecord nRec) {
			this.lRec = lRec;
			this.nRec = nRec;
		}
		
	}

	
}
