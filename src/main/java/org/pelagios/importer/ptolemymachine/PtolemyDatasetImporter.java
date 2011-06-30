package org.pelagios.importer.ptolemymachine;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.DataRecordBuilder;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.exception.DatasetExistsException;
import org.pelagios.graph.exception.DatasetNotFoundException;
import org.pelagios.importer.AbstractDatasetImporter;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Importer for the Ptolemy Machine dataset. RDF/XML sample:
 * 
 *     <oac:Annotation
 *       rdf:about="http://shot.holycross.edu:8080/ptolgeoinv/id/pt-ll-1">
 *       <oac:hasBody>
 *           <rdf:Description
 *               rdf:about="urn:cite:ptolemymachine:pt-ll-1">
 *               <dcterms:source
 *                   rdf:resource="http://shot.holycross.edu:8080/ptolgeoinv/id/pt-ll-1"/>
 *           </rdf:Description>
 *       </oac:hasBody>
 *       <oac:hasTarget>
 *           <rdf:Description
 *               rdf:about="urn:cts:greekLit:tlg0363.tlg009.chs01:2.2.1:">
 *               <dcterms:isPartOf
 *                   rdf:resource="urn:cts:greekLit:tlg0363.tlg009.chs01"/>
 *               <dcterms:source
 *                   rdf:resource="http://ptolemymachine.appspot.com/CTS?request=GetPassagePlus&amp;urn=urn:cts:greekLit:tlg0363.tlg009.chs01:2.2.1"/>
 *           </rdf:Description>
 *       </oac:hasTarget>
 *       <dc:title
 *           xml:lang="en">Ptolemy's Geography: Boreion akron</dc:title>
 *       <dcterms:creator>Neel Smith</dcterms:creator>
 *       <dcterms:created>2011-05-01</dcterms:created>
 *   </oac:Annotation>
 *   
 * To introduce some structuring in the Ptolemy Machine data, I took the liberty of
 * defining two levels of hierarchy, based on the URN format:
 * 
 *   urn:cts:greekLit:tlg0363.tlg009.chs01:2.2.1:
 *   
 * Level one is all annotations with same N: [...]tlg009.chs01:N.x.x
 * 
 * Level two is all annotations with same N and M: [...]tlg009.chs01:N.M.x
 * 
 * @author Rainer Simon
 */
public class PtolemyDatasetImporter extends AbstractDatasetImporter {
	
	private DatasetBuilder rootNode = new DatasetBuilder("Ptolemy Machine");
	
	public PtolemyDatasetImporter(File rdf) {
		super(rdf);
	}
	
	/**
	 * Imports the RDF data into the Pelagios Graph.
	 * @param graph the graph
	 * @throws DatasetExistsException 
	 * @throws PlaceNotFoundException 
	 */
	@Override
	public void importData(PelagiosGraph graph) throws DatasetExistsException {
		// Start by creating the root node
		graph.addDataset(rootNode);
		
		HashMap<Hierarchy, List<DataRecordBuilder>> allRecords = 
			new HashMap<Hierarchy, List<DataRecordBuilder>>();
		
		for (Resource oac : listOACAnnotations()) {
			// Annotation title ... place name
			// String title = oac.getProperty(DC.title).getObject().toString();

			// Body DCTerms source = place URI
			// RDFNode body = oac.getProperty(OAC_HASBODY).getObject();
			// String placeURI = body.asResource().getProperty(DCTerms.source)
			// 	  .getObject().toString();

			// Target description = data record URN
			RDFNode target = oac.getProperty(OAC_HASTARGET).getObject();
			String recordURN = target.toString();
			
			// Target DCTerms source = data record URL
			String recordURL = target.asResource().getProperty(DCTerms.source)
				.getObject().toString();

			// Create the record and store in memory - we'll batch-add 
			// all records to the graph later for added performance
			Hierarchy h = getHierarchy(recordURN);
			try {
				List<DataRecordBuilder> records = allRecords.get(h);
				if (records == null) {
					records = new ArrayList<DataRecordBuilder>();
					allRecords.put(h, records);
				}
				records.add(new DataRecordBuilder(new URI(recordURL)));
			} catch (URISyntaxException e) {
				// Only happens in case of data set errors - we have a 
				// zero-tolerance policy for those kinds of things
				throw new RuntimeException(e);
			}
		}
		
		batchAddAll(allRecords, graph);
	}
	
	private void batchAddAll(HashMap<Hierarchy, List<DataRecordBuilder>> records, PelagiosGraph graph) {	
		for (Hierarchy h : records.keySet()) {
			DatasetBuilder parent = getDatasetBuilder(h, graph);
			try {
				graph.addDataRecords(records.get(h), parent);
			} catch (DatasetNotFoundException e) {
				// Can never happen
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Utility method which creates a DatasetBuilder for the specified sub-set. The method
	 * also makes sure the sub-set and parent set are created in the graph if they don't
	 * exist already
	 * @param parentIdx the parent-level index
	 * @param subsetIdx the subset-level index
	 * @param graph the Pelagios graph
	 * @return the DatasetBuilder
	 */
	private DatasetBuilder getDatasetBuilder(Hierarchy h, PelagiosGraph graph) {
		String parentName = "Ptolemy Machine " + h.parentIdx;
		String subsetName = "Ptolemy Machine " + h.parentIdx + ":" + h.subsetIdx;
		
		// Create parent data set (if it doesn't exist)
		try {
			graph.addDataset(new DatasetBuilder(parentName), rootNode);
		} catch (DatasetNotFoundException e) {
			// Can never happen
			throw new RuntimeException(e);
		} catch (DatasetExistsException e) {
			// Already exists - never mind
		}
		
		// Create sub set (if it doesn't exist)
		try {
			graph.addDataset(
					new DatasetBuilder(subsetName),
					new DatasetBuilder(parentName));
		} catch (DatasetNotFoundException e) {
			// Can never happen
			throw new RuntimeException(e);
		} catch (DatasetExistsException e) {
			// Already exists - never mind
		}
		
		return new DatasetBuilder(subsetName);
	}
	
	/**
	 * Utility method which strips out the 'hierarchy' part of
	 * the URN, e.g. "4.7" in case of 
	 * 
	 *   urn:cts:greekLit:tlg0363.tlg009.chs01:4.7.10:???????????????
	 * 
	 * and returns it as a two-element array, e.g. { 4, 7 } in the
	 * above case.
	 * 
	 * @param urn the urn
	 * @return the hierarchy indexes
	 */
	Hierarchy getHierarchy(String urn) {
		StringTokenizer tokenizer = new StringTokenizer(urn, ":");

		if (tokenizer.countTokens() < 5)
			throw new RuntimeException("Illegal URN format - looks like an error in the dataset: " + urn);
			
		// Skip the first 4 tokens
		for (int i=0; i<4; i++)
			tokenizer.nextToken();
		
		String s[] = tokenizer.nextToken().split("\\.");
		if (s.length < 2)
			throw new RuntimeException("Illegal URN format - looks like an error in the dataset: " + urn);
		
		return new Hierarchy(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
	}
	
}
