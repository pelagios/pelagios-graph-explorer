package org.pelagios.graph;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.pelagios.explorer.Config;
import org.pelagios.graph.builder.PelagiosGraphBuilder;
import org.pelagios.graph.builder.PlaceBuilder;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.exceptions.PlaceExistsException;
import org.pelagios.graph.importer.AbstractDatasetImporter;
import org.pelagios.graph.importer.pleiades.PleiadesImporter;

public class PelagiosGraphAdmin {
	
	private String neo4jDir;
	
	private PelagiosGraph graph;
	
	public PelagiosGraphAdmin() {
		this(Config.getInstance().getNeo4jDirectory());
	}
	
	public PelagiosGraphAdmin(String neo4jDir) {
		this.neo4jDir = neo4jDir;
	}
		
	private PelagiosGraph getDatabase() {
		PelagiosGraphBuilder builder = 
			new PelagiosGraphBuilder(neo4jDir);
		return builder.build();		
	}
	
	public void importPleiades(File locationsCSV, File namesCSV)
		throws IOException, PlaceExistsException {
		
		PleiadesImporter importer = new PleiadesImporter();
		List<PlaceBuilder> places =
			importer.importPleiadesDump(locationsCSV, namesCSV);
		graph.addPlaces(places);
	}
	
	public void importDataset(AbstractDatasetImporter importer)
		throws DatasetExistsException {
		
		importer.importData(graph);
	}
	
	public void shutdown() {
		graph.shutdown();
	}
	
	public void clearDatabase() {
		delete(new File(neo4jDir));
		graph = getDatabase();
	}
	
	private void delete(File file) {
	    if (file.exists()) {
	        if (file.isDirectory() ) {
	            for (File child : file.listFiles()) {
	            	delete(child);
	            }
	        }
	        file.delete();
	    }
	}

}
