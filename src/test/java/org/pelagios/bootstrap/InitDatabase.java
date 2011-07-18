package org.pelagios.bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.PelagiosGraphBuilder;
import org.pelagios.graph.builder.PlaceBuilder;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.exceptions.PlaceExistsException;
import org.pelagios.graph.importer.gap.GAPImporter;
import org.pelagios.graph.importer.nomisma.NomismaDatasetImporter;
import org.pelagios.graph.importer.perseus.PerseusImporter;
import org.pelagios.graph.importer.pleiades.PleiadesImporter;
import org.pelagios.graph.importer.ptolemymachine.PtolemyDatasetImporter;
import org.pelagios.graph.importer.spqr.SPQRImporter;
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
	
	private static final String DATASETS_DIR = "src/test/resources/datasets/";
	
	private static File PLEIADES_LOCATIONS_CSV = null;
	private static File PLEIADES_NAMES_CSV = null;
	
	private static final String NOMISMA_RDF = DATASETS_DIR + "nomisma.org.rdf";
	
	private static final String PTOLEMY_MACHINE_RDF = DATASETS_DIR + "ptolemy.rdf";
	
	private static final String PERSEUS_GRECO_ROMAN_RDF = DATASETS_DIR + "perseus-greco-roman.rdf";
	private static final String PERSEUS_RENAISSANCE_RDF = DATASETS_DIR + "perseus-renaissance.rdf";
	private static final String PERSEUS_RICHMOND_TIMES_RDF = DATASETS_DIR + "perseus-richmond-times.rdf";
	
	private static final String GAP_N3 = DATASETS_DIR + "GAPtriples.n3";
	
	private static final String SPQR_DIR = DATASETS_DIR + "spqr/downloads";
	
	static {
		// Look for Pleiades dump files
		File resourcesDir = new File("src/test/resources");
		for (File f : resourcesDir.listFiles()) {
			if (f.getName().startsWith("pleiades-locations") &&
				f.getName().endsWith(".csv")) {
				
				PLEIADES_LOCATIONS_CSV = f;
			} else if (f.getName().startsWith("pleiades-names") &&
				f.getName().endsWith(".csv")) {
				
				PLEIADES_NAMES_CSV = f;
			}
		}
		
		if ((PLEIADES_LOCATIONS_CSV == null) || (PLEIADES_NAMES_CSV == null)) {
			throw new RuntimeException("Error: Pleiades dump files not found!");
		}
	}
	
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
		log.info("Importing Google Ancient Places dataset");	
		importGAP(graph);
		log.info("Done. (" + (System.currentTimeMillis() - taskStart) + " ms)");

		taskStart = System.currentTimeMillis();
		log.info("Importing Ptolemy Machine dataset");	
		importPtolemyMachine(graph);
		log.info("Done. (" + (System.currentTimeMillis() - taskStart) + " ms)");
		
		taskStart = System.currentTimeMillis();
		log.info("Importing Perseus dataset");	
		importPerseus(graph);
		log.info("Done. (" + (System.currentTimeMillis() - taskStart) + " ms)");	
		
		taskStart = System.currentTimeMillis();
		log.info("Importing SPQR dataset");	
		importSPQR(graph);
		log.info("Done. (" + (System.currentTimeMillis() - taskStart) + " ms)");

		graph.shutdown();
		log.info("Database initialized. Took " + 
				(System.currentTimeMillis() - initStart)/1000 + " seconds.");
	}
	
	private static void importPleiades(PelagiosGraph graph)
		throws IOException, PlaceExistsException {
		
		PleiadesImporter importer = new PleiadesImporter();
		List<PlaceBuilder> places = importer.importPleiadesDump(
				PLEIADES_LOCATIONS_CSV,
				PLEIADES_NAMES_CSV);
		
		graph.addPlaces(places);
	}
	
	private static void importNomisma(PelagiosGraph graph) throws DatasetExistsException {
		NomismaDatasetImporter importer = new NomismaDatasetImporter(new File(NOMISMA_RDF));
		importer.importData(graph);
	}
	
	private static void importGAP(PelagiosGraph graph) throws DatasetExistsException {
		GAPImporter importer = new GAPImporter(new File(GAP_N3));
		importer.importData(graph);
	}
	
	private static void importPtolemyMachine(PelagiosGraph graph) throws DatasetExistsException {
		PtolemyDatasetImporter importer = new PtolemyDatasetImporter(new File(PTOLEMY_MACHINE_RDF));
		importer.importData(graph);
	}
	
	private static void importPerseus(PelagiosGraph graph) throws DatasetExistsException {
		HashMap<String, File> perseusFiles = new HashMap<String, File>();
		perseusFiles.put("Perseus Greco-Roman", new File(PERSEUS_GRECO_ROMAN_RDF));
		perseusFiles.put("Perseus Renaissance", new File(PERSEUS_RENAISSANCE_RDF));
		perseusFiles.put("Perseus Richmond Times", new File(PERSEUS_RICHMOND_TIMES_RDF));
		new PerseusImporter(perseusFiles, graph);
	}
	
	private static void importSPQR(PelagiosGraph graph) throws DatasetExistsException {
		new SPQRImporter(new File(SPQR_DIR), graph);
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
