package org.pelagios.importer;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.exception.DatasetExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
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
	protected String OAC_NAMESPACE = "http://www.openannotation.org/ns/";
	
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
	 * The logger
	 */
	protected Logger log = LoggerFactory.getLogger(AbstractDatasetImporter.class);
	
	/**
	 * The base constructor parses an RDF file to a Jena model
	 * for further processing.
	 * @param rdf the RDF file
	 */
	public AbstractDatasetImporter(File rdf) {
		InputStream is = FileManager.get().open(rdf.getAbsolutePath());
		model = ModelFactory.createDefaultModel();
		model.read(is, null);
		
		OAC_HASBODY = model.createProperty(OAC_NAMESPACE, "hasBody");
		OAC_HASTARGET = model.createProperty(OAC_NAMESPACE, "hasTarget");
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
	 * Utility method that lists all OAC bodies in 
	 * the Jena model by their URIs
	 * @return the body URIs
	 */
	protected List<URI> listOACBodies() {
		NodeIterator it = model
				.listObjectsOfProperty(OAC_HASBODY);
		return listResourceURIs(it);
	}

	/**
	 * Utility method that lists all OAC targets
	 * in the Jena model by their URIs
	 * @return the target URIs
	 */
	protected List<URI> listOACTargets() {
		NodeIterator it = model
			.listObjectsOfProperty(OAC_HASTARGET);
		return listResourceURIs(it);
	}
	
	/**
	 * Utility method that compiles a list of URIs from
	 * a NodeIterator.
	 * @param it the iterator
	 * @return the list of URIs
	 */
	private List<URI> listResourceURIs(NodeIterator it) {
		List<URI> uris = new ArrayList<URI>();
		while (it.hasNext()) {
			Resource resource = it.next().asResource();
			try {
				uris.add(new URI(resource.toString()));
			} catch (URISyntaxException e) {
				log.warn("Could not resolve URI. Might be a problem with the dataset. " +
					e.getMessage());
			}		
		}
		return uris;
	}
	
	protected class Hierarchy {
		
		public int parentIdx, subsetIdx;
		
		public Hierarchy(int parentIdx, int subsetIdx) {
			this.parentIdx = parentIdx;
			this.subsetIdx = subsetIdx;
		}
		
		@Override
		public boolean equals(Object arg) {
			if (!(arg instanceof Hierarchy))
				return false;
			
			Hierarchy other = (Hierarchy) arg;
			
			if (other.subsetIdx != subsetIdx)
				return false;
			
			if (other.parentIdx != parentIdx)
				return false;
			
			return true;
		}
	}

}
