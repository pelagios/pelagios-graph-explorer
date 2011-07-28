package org.pelagios.graph.importer.pleiades;

import java.io.File;
import java.io.FileNotFoundException;

public class PleiadesDumpFiles {
	
	private File locationsCSV, namesCSV;
	
	public PleiadesDumpFiles(File dir) throws FileNotFoundException {
		for (File f : dir.listFiles()) {
			if (f.getName().startsWith("pleiades-locations") &&
				f.getName().endsWith(".csv")) {
				locationsCSV = f;
			} else if (f.getName().startsWith("pleiades-names") &&
				f.getName().endsWith(".csv")) {
				namesCSV = f;
			}
		}
		
		if ((locationsCSV == null) || (namesCSV == null)) {
			throw new FileNotFoundException("Error: Pleiades dump files not found!");
		}
	}
	
	public File getLocationsCSV() {
		return locationsCSV;
	}
	
	public File getNamesCSV() {
		return namesCSV;
	}
	
}
