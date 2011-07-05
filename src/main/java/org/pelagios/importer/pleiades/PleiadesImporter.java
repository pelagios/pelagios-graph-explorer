package org.pelagios.importer.pleiades;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.builder.PlaceBuilder;
import org.pelagios.backend.graph.exception.PlaceExistsException;
import org.pelagios.importer.pleiades.locations.LocationParser;
import org.pelagios.importer.pleiades.locations.LocationRecord;
import org.pelagios.importer.pleiades.names.NameParser;
import org.pelagios.importer.pleiades.names.NameRecord;

import com.vividsolutions.jts.geom.Coordinate;

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
					Coordinate c = lRec.getGeometry().getShape().getCentroid();
					records.add(new PlaceBuilder(
							nRec.getNameTransliterated(), 
							c.x, c.y,
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