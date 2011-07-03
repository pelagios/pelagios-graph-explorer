package org.pelagios.importer.ptolemymachine;

import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public class PtolemyPlaceIndexer {
	
	public void parseCSV() throws IOException {
		// TODO just a sample on how to use OpenCSV!
		CSVReader reader = new CSVReader(new FileReader("yourfile.csv"));
	    String [] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
	        // nextLine[] is an array of values from the line
	        System.out.println(nextLine[0] + nextLine[1] + "etc...");
	    }		
	}

}
