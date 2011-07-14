package org.pelagios.pleiades.importer.names;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.pelagios.pleiades.importer.PleiadesCSVParser;

import au.com.bytecode.opencsv.CSVReader;

public class NameParser extends PleiadesCSVParser {
	
	public HashMap<String, NameRecord> parseNamesCSV(File csv) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(csv), ',', '\"', 1);
		
		HashMap<String, NameRecord> names = new HashMap<String, NameRecord>();
		int ctr = 0;
		int invalids = 0;
		int nonUniques = 0;
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
	    	ctr++;
	    	if (nextLine.length != 15) {
	    		log.warn("Invalid CSV line - skipping " + (ctr + 1) + ": " + logLine(nextLine));
	    	} else {
	    		try {
	    			NameRecord nr = new NameRecord();	   	    		
	   	    		nr.setPid(nextLine[9]);
	   	    		nr.setNameTransliterated(nextLine[7]);
	   	    		
	   	    		if (names.containsKey(nr.getPid()))
	   	    			nonUniques++;
	   	    		
	   	    		names.put(nr.getPid(), nr);
	   	    	} catch (Exception e) {
	   	    		invalids++;
	   	    		log.warn("Skipping invalid Pleiades name record: " + e.getMessage());
	   	    	}
	    	}
	    }	
	    
	    log.info("Name parse results:");
	    log.info(ctr + " lines total");
	    log.info(invalids + " invalid name records");
	    log.info(nonUniques + " non-unique PIDs");
	    log.info(names.size() + " unique name records parsed successfully");
	    return names;
	}
	
}
