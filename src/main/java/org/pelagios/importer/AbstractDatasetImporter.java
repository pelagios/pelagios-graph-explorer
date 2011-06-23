package org.pelagios.importer;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.pelagios.graph.Dataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

/**
 * The abstract base class for Pelagios datasets.
 * 
 * @author Rainer Simon
 */
public abstract class AbstractDatasetImporter implements Dataset {
	
	/**
	 * String constants
	 */
	protected String OAC_NAMESPACE = "http://www.openannotation.org/ns/";	
	protected String OAC_HAS_BODY = "hasBody";
	protected String OAC_HAS_TARGET = "hasTarget";

	/**
	 * The dataset RDF model
	 */
	protected Model model;
	
	protected Logger log = LoggerFactory.getLogger(AbstractDatasetImporter.class);
	
	public AbstractDatasetImporter(File rdf) {
		InputStream is = FileManager.get().open(rdf.getAbsolutePath());
		model = ModelFactory.createDefaultModel();
		model.read(is, null);
	}
	
	/**
	 * Utility method that lists all OAC annotations
	 * in this model. 
	 * @return the annotations
	 */
	protected List<Resource> listAnnotations() {
		return model
			.listResourcesWithProperty(model.createProperty(OAC_NAMESPACE, OAC_HAS_BODY))
			.toList();
	}
	
	/**
	 * Lists all referenced unique places in this dataset
	 * @return the places
	 */
	public List<URI> listPlaces() {
		NodeIterator it = model
				.listObjectsOfProperty(model.createProperty(OAC_NAMESPACE, OAC_HAS_BODY));
		
		List<URI> places = new ArrayList<URI>();
		while (it.hasNext()) {
			Resource resource = it.next().asResource();
			try {
				places.add(new URI(resource.toString()));
			} catch (URISyntaxException e) {
				log.warn("Could not resolve URI. Might be a problem with the dataset. " +
					e.getMessage());
			}
		}
		return places;
	}

	public List<String> listTargets() {
		NodeIterator it = model
			.listObjectsOfProperty(model.createProperty(OAC_NAMESPACE, OAC_HAS_TARGET));
		
		List<String> targets = new ArrayList<String>();
		while (it.hasNext()) {
			targets.add(it.next().toString());
		}
		return targets;
		
	}

}
