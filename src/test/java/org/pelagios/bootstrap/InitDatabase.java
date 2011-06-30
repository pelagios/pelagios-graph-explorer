package org.pelagios.bootstrap;

import java.io.File;
import java.io.IOException;

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.PelagiosGraphBuilder;
import org.pelagios.graph.exception.DatasetExistsException;
import org.pelagios.graph.exception.PlaceExistsException;
import org.pelagios.importer.nomisma.NomismaDatasetImporter;
import org.pelagios.importer.ptolemymachine.PtolemyDatasetImporter;
import org.pelagios.pleiades.PleiadesImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that (re)initializes the graph database
 * with Pelagios data from scratch. Note: existing contents
 * will be deleted. Use with caution!
 *  
 * TODO generate some sort of import report (total no. of
 * imported records, fails, performance stats etc.)
 * 
 * TODO do this in Groovy (try out Groovy strings :-)
 *  
 * @author Rainer Simon
 */
public class InitDatabase {
	
	private static Logger log = LoggerFactory.getLogger(InitDatabase.class); 
	
	// TODO make file paths configurable via .properties file!
	
	private static final String NEO4J_DIR = "c:/neo4j-data";
	
	private static final String PLEIADES_LOCATIONS_CSV = "src/test/resources/pleiades-locations-20110627.csv";
	private static final String PLEIADES_NAMES_CSV = "src/test/resources/pleiades-names-20110627.csv";
	
	private static final String NOMISMA_RDF = "src/test/resources/datasets/nomisma.org.pelagios.rdf";
	
	private static final String PTOLEMY_MACHINE_RDF = "src/test/resources/datasets/ptolemy-oac.rdf";
	
	public static void main(String[] args) 
		throws IOException, PlaceExistsException, DatasetExistsException {
		
		long initStart = System.currentTimeMillis();
		
		long taskStart = initStart;
		log.info("Dropping existing database (if any)");
		delete(new File(NEO4J_DIR));
		log.info("Done. (" + (System.currentTimeMillis() - taskStart) + " ms)");
		
		taskStart = System.currentTimeMillis();
		log.info("Initializing neo4j");		
		PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(NEO4J_DIR);
		PelagiosGraph graph = graphBuilder.build();		
		log.info("Done. (" + (System.currentTimeMillis() - taskStart) + " ms)");

		taskStart = System.currentTimeMillis();
		log.info("Importing Pleiades Gazetteer");		
		importPleiades(graph);
		log.info("Done. (" + (System.currentTimeMillis() - taskStart) + " ms)");

		taskStart = System.currentTimeMillis();
		log.info("Importing nomisma dataset");	
		importNomisma(graph);
		log.info("Done. (" + (System.currentTimeMillis() - taskStart) + " ms)");

		taskStart = System.currentTimeMillis();
		log.info("Importing Ptolemy Machine dataset");	
		importPtolemyMachine(graph);
		log.info("Done. (" + (System.currentTimeMillis() - taskStart) + " ms)");
		
		graph.shutdown();
		log.info("Database initialized. Took " + 
				(System.currentTimeMillis() - initStart)/1000 + " seconds.");
	}
	
	private static void importPleiades(PelagiosGraph graph)
		throws IOException, PlaceExistsException {
		
		PleiadesImporter importer = new PleiadesImporter();
		importer.importPleiadesDump(
				new File(PLEIADES_LOCATIONS_CSV),
				new File(PLEIADES_NAMES_CSV),
				graph);
	}
	
	private static void importNomisma(PelagiosGraph graph) throws DatasetExistsException {
		NomismaDatasetImporter importer = new NomismaDatasetImporter(new File(NOMISMA_RDF));
		importer.importData(graph);
	}
	
	private static void importPtolemyMachine(PelagiosGraph graph) throws DatasetExistsException {
		PtolemyDatasetImporter importer = new PtolemyDatasetImporter(new File(PTOLEMY_MACHINE_RDF));
		importer.importData(graph);
	}
	
	private static void delete(File file) {
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