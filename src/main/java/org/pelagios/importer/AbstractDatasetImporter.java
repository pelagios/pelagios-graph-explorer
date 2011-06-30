package org.pelagios.importer;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.DataRecordBuilder;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.exception.DatasetExistsException;
import org.pelagios.graph.exception.DatasetNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

/**
 * An abstract base class that includes functionality which
 * should be useful for importing the average Pelagios RDF
 * data set. As all data sets are different, each importer 
 * will require a few (or many) adaptations. 
 * 
 * @author Rainer Simon
 */
public abstract class AbstractDatasetImporter {
	
	/**
	 * String constants
	 */
	protected static final String OAC_NAMESPACE = "http://www.openannotation.org/ns/";

	/**
	 * OAC hasBody property
	 */
	protected Property OAC_HASBODY;
	
	/**
	 * OAC hasTarget property
	 */
	protected Property OAC_HASTARGET;

	/**
	 * The dataset RDF model
	 */
	protected Model model;
	
	/**
	 * The top-level root node for this data set
	 */
	protected DatasetBuilder rootNode;
	
	/**
	 * The logger
	 */
	protected Logger log = LoggerFactory.getLogger(AbstractDatasetImporter.class);
	
	/**
	 * The base constructor parses an RDF file to a Jena model
	 * for further processing.
	 * @param rdf the RDF file
	 */
	public AbstractDatasetImporter(File rdf, DatasetBuilder rootNode) {
		InputStream is = FileManager.get().open(rdf.getAbsolutePath());
		model = ModelFactory.createDefaultModel();
		model.read(is, null);
		
		OAC_HASBODY = model.createProperty(OAC_NAMESPACE, "hasBody");
		OAC_HASTARGET = model.createProperty(OAC_NAMESPACE, "hasTarget");
		
		this.rootNode = rootNode;
	}
	
	public abstract void importData(PelagiosGraph graph) throws DatasetExistsException;
	
	/**
	 * Utility method that lists all OAC annotations
	 * in the Jena model. 
	 * @return the annotations
	 */
	protected List<Resource> listOACAnnotations() {
		return model
			.listResourcesWithProperty(OAC_HASBODY)
			.toList();
	}

	/**
	 * Utility method which adds a list of (hierarchical) data records
	 * to the graph.
	 * @param records the records
	 * @param graph the graph
	 */
	protected void batchAdd(HashMap<Hierarchy, List<DataRecordBuilder>> records, PelagiosGraph graph) {
		for (Hierarchy h : records.keySet()) {
			try {
				DatasetBuilder parent = buildHierarchy(h, graph);
				graph.addDataRecords(records.get(h), parent);
			} catch (DatasetNotFoundException e) {
				// Can never happen
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Checks whether the specified hierarchy exists in the graph,
	 * and creates it if it doesn't.
	 * @param hierarchy the hierarchy
	 * @param graph the graph
	 * @return the lowest level DatasetBuilder in the hierarchy
	 */
	private DatasetBuilder buildHierarchy(Hierarchy hierarchy, PelagiosGraph graph) {
		
		// Starting at the top, check whether all hierarchy levels
		// exist in the graph - and create them if they don't
		DatasetBuilder parentLvl = rootNode;
		for (String lvl : hierarchy.getLevels()) {
			DatasetBuilder currentLvl = new DatasetBuilder(lvl);
			try {
				graph.addDataset(currentLvl, parentLvl);
			} catch (DatasetExistsException e) {
				// Already exists - never mind
			} catch (DatasetNotFoundException e) {
				// Can never happen
				throw new RuntimeException(e);
			} finally {
				parentLvl = currentLvl;
			}
		}
		return parentLvl;
	}

}
