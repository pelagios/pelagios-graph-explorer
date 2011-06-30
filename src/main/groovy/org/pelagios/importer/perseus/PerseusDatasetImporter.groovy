package org.pelagios.importer.perseus

import java.io.File

import org.pelagios.graph.PelagiosGraph
import org.pelagios.graph.builder.DatasetBuilder
import org.pelagios.graph.exception.DatasetExistsException
import org.pelagios.importer.AbstractDatasetImporter

/**
 * Importer for the Perseus data set collection. RDF/XML sample:
 * 
 * <oac:Annotation rdf:about="org.perseus:entityoccurrence:1867133:1:Perseus:text:1999.02.0025:book=3:poem=17">
 *   <oac:hasBody>
 *     <rdf:Description rdf:about="http://pleiades.stoa.org/places/432839"/>
 *   </oac:hasBody>
 *   <oac:hasTarget>
 *     <rdf:Description rdf:about="http://www.perseus.tufts.edu/hopper/xmlchunk?doc=Perseus:text:1999.02.0025:book=3:poem=17"/>
 *   </oac:hasTarget>
 *   <dc:title xml:lang="en">Formiae (Italy)</dc:title>
 *   <dcterms:creator>Perseus Digital Library</dcterms:creator>
 *   <dcterms:created>2011-05-19</dcterms:created>
 * </oac:Annotation>
 * 
 * @author Rainer Simon
 */
public class PerseusDatasetImporter extends AbstractDatasetImporter {

	private DatasetBuilder rootNode = new DatasetBuilder("Perseus")
	
	public PerseusDatasetImporter(File rdf) {
		super(rdf)
	}

	@Override
	public void importData(PelagiosGraph graph) throws DatasetExistsException {
		// Start by creating the root node
		graph.addDataset(rootNode)

		
	}

}
