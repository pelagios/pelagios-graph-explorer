package org.pelagios.graph.importer.pleiades;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Utility class that takes a directory path and searches for a Pleiades 'locations' and 'names'
 * dump file. The class ignores the data information which is part of the filename, so that
 * dump files with arbitrary dates can be used.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 *
 */
public class PleiadesDumpFiles {

    private File locationsCSV, namesCSV;

    public PleiadesDumpFiles(File dir) throws FileNotFoundException {
        for (File f : dir.listFiles()) {
            if (f.getName().startsWith("pleiades-locations") && f.getName().endsWith(".csv")) {
                locationsCSV = f;
            } else if (f.getName().startsWith("pleiades-names") && f.getName().endsWith(".csv")) {
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
