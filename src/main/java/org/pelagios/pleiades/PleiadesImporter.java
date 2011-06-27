package org.pelagios.pleiades;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pelagios.geo.shape.Point;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.PlaceBuilder;
import org.pelagios.graph.exception.PlaceExistsException;
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
	 * @throws PlaceExistsException if one (or more) place(s) in the dump are duplicates
	 */
	public void importPleiadesDump(File locationsCSV, File namesCSV, PelagiosGraph graph)
		throws IOException, PlaceExistsException {
		
		LocationParser lp = new LocationParser();
		HashMap<String, LocationRecord> locations = lp.parseLocationsCSV(locationsCSV);
		
		NameParser np = new NameParser();
		HashMap<String, NameRecord> names = np.parseNamesCSV(namesCSV);
		
		graph.addPlaces(mergeRecords(locations, names));
	}
	
	private List<PlaceBuilder> mergeRecords(
			HashMap<String, LocationRecord> locations,
			HashMap<String, NameRecord> names) {
		
		List<PlaceBuilder> records = new ArrayList<PlaceBuilder>();	
		
		for (LocationRecord lRec : locations.values()) {
			NameRecord nRec = names.get(lRec.getPid());
			if (nRec != null) {
				try {
					Point p = lRec.getGeometry().getShape().getCentroid();
					records.add(new PlaceBuilder(
							nRec.getNameTransliterated(), 
							p.getLon(),
							p.getLat(),
							new URI(PLEIADES_BASE_URI + nRec.getPid())
					));
				} catch (URISyntaxException e) {
					// Should never happen
					throw new IllegalArgumentException(e);
				}
			}
		}
		
		return records;
	}
	
}
