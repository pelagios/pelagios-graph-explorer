package org.pelagios.pleiades.locations;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.pelagios.pleiades.PleiadesCSVParser;

import au.com.bytecode.opencsv.CSVReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LocationParser extends PleiadesCSVParser {
	
	public HashMap<String, LocationRecord> parseLocationsCSV(File csv) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(csv), ',', '\"', 1);
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Geometry.class, new GeometryDeserializer());
		Gson gson = builder.create();
		
		HashMap<String, LocationRecord> locations = new HashMap<String, LocationRecord>();
		int ctr = 0;
		int invalids = 0;
		int nonUniques = 0;
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
	    	ctr++;
	    	if (nextLine.length != 13) {
	    		log.warn("Invalid CSV line - skipping " + (ctr + 1) + ": " + logLine(nextLine));
	    		invalids++;
	    	} else {
	    		try {
	    			if (nextLine[3].isEmpty() || nextLine[3].toLowerCase().equals(JSON_NULL)) {
	    				log.warn("No geometry - skipping line " + (ctr + 1) + ": " + logLine(nextLine));
	    				invalids++;
	    			} else {
		    			LocationRecord lr = new LocationRecord();	   	    		
		   	    		lr.setPid(nextLine[7]);
		   	    		lr.setCreators(nextLine[1]);
		   	    		lr.setDescription(nextLine[2]);		   	    		
		   	    		lr.setGeometry(gson.fromJson(nextLine[3], Geometry.class));
		   	    		
		   	    		if (locations.containsKey(lr.getPid()))
		   	    			nonUniques++;
		   	    		
		   	    		locations.put(lr.getPid(), lr);
	    			}
	   	    	} catch (Exception e) {
	   	    		invalids++;
	   	    		log.warn("Skipping invalid Pleiades location record: " + e.getMessage());
	   	    	}
	    	}
	    }	
	    
	    log.info("Location parse results:");
	    log.info(ctr + " lines total");
	    log.info(invalids + " invalid location records");
	    log.info(nonUniques + " non-unique PIDs");
	    log.info(locations.size() + " unique location records parsed successfully");
	    return locations;
	}
	
}
