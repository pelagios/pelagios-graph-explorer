package org.pelagios.graph.importer.perseus

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.GeoAnnotationBuilder;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.builder.PelagiosGraphImpl;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.importer.AbstractDatasetImporter;
import org.pelagios.graph.importer.Hierarchy;

import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.vocabulary.DC;

/**
 * Importer for one Perseus data file. RDF/XML sample:
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
 * To introduce some structuring in the Perseus data, I took the liberty of
 * defining two levels of hierarchy, based on the URI format:
 * 
 *   org.perseus:entityoccurrence:1867133:1:Perseus:text:1999.02.0025:book=3:poem=17
 *   
 * Level one is all annotations with same 'text' numbers (e.g. 1999.02.005)
 * 
 * Level two is all annotations with same book number (e.g. 3)
 * 
 * @author Rainer Simon
 */
class PerseusDatafileImporter extends AbstractDatasetImporter {
	
	private String name
	
	public PerseusDatafileImporter(String name, File rdf, DatasetBuilder rootNode) {
		super(rdf, rootNode)
		this.name = name
	}

	/**
	 * Imports the RDF data into the Pelagios Graph.
	 * @param graph the graph
	 * @throws DatasetExistsException 
	 * @throws PlaceNotFoundException 
	 */
	@Override
	public void importData(PelagiosGraph graph) throws DatasetExistsException {
		// Start by creating the collection root node and making this
		// the root of this dataset
		DatasetBuilder newRoot = new DatasetBuilder(name)
		graph.addDataset(newRoot, rootNode)
		rootNode = newRoot;
		
		HashMap<Hierarchy, List<GeoAnnotationBuilder>> allRecords = 
			new HashMap<Hierarchy, List<GeoAnnotationBuilder>>()
		
		for (Resource oac : listOACAnnotations()) {			
			// Annotation URI for building the hierarchy
			Hierarchy h = getHierarchy(oac.toString())
			
			// Target = data record URN
			String recordURL = oac.getProperty(OAC_HASTARGET).getObject().asResource().getURI()
			
			// Body = Pleiades Place
			String pleiadesURL = oac.getProperty(OAC_HASBODY).getObject().toString()

			// Title
			String title = oac.getProperty(DC.title).getObject().asLiteral().getString()
			
			// Create the record and store in memory - we'll batch-add 
			// all records to the graph later for added performance
			try {
				List<GeoAnnotationBuilder> records = allRecords.get(h)
				if (records == null) {
					records = new ArrayList<GeoAnnotationBuilder>()
					allRecords.put(h, records)
				}
				GeoAnnotationBuilder annotation = new GeoAnnotationBuilder(
                    new URI(recordURL.replace("xmlchunk", "text").replace(" ", "%20")),
                    new URI(pleiadesURL));
				
				annotation.setLabel(title)
				records.add(annotation)
			} catch (URISyntaxException e) {
				// Only happens in case of data set errors - we have a 
				// zero-tolerance policy for those kinds of things
				throw new RuntimeException(e);
			}
		}
		
		batchAdd(allRecords, graph);
	}
	

	
	/**
	* Utility method which strips out the 'hierarchy' from the
	* the URI, e.g. 1999 > 01 > 0084 > Book 12 in case of
	* 
	*   org.perseus:entityoccurrence:1867974:1:Perseus:text:1999.01.0084:book=12:chapter=36
	* 
	* @param uri the URI
	* @return the hierarchy
	*/
   Hierarchy getHierarchy(String uri) {
	   List<String> hierarchy = new ArrayList<String>()
	   hierarchy.add(name);
	   
	   StringTokenizer tokenizer = new StringTokenizer(uri, ":")
	   if (tokenizer.countTokens() < 8)
		   throw new RuntimeException("Illegal URI format - looks like an error in the dataset: " + uri)
		   
	   // Skip the first 6 tokens
	   for (int i=0; i<6; i++)
		   tokenizer.nextToken()
		   
	   // E.g. '1999.01.0084'
	   String prefix = tokenizer.nextToken();
	   hierarchy.add(prefix)
	   
	   // Look for 'book', 'chapter', 'section', 'speech', 'narrative' and 'poem'
       while (tokenizer.hasMoreTokens()) {
		   String nextToken = tokenizer.nextToken()
		   if (nextToken.startsWith('book=')) {
			   hierarchy.add('Book ' + nextToken.substring(nextToken.lastIndexOf('=') + 1))
		   } else if (nextToken.startsWith('chapter')) {
		       hierarchy.add('Chapter ' + nextToken.substring(nextToken.lastIndexOf('=') + 1))
		   } else if (nextToken.startsWith('section')) {
		       hierarchy.add('Section ' + nextToken.substring(nextToken.lastIndexOf('=') + 1))
		   } else if (nextToken.startsWith('speech')) {
		       hierarchy.add('Speech ' + nextToken.substring(nextToken.lastIndexOf('=') + 1))
		   } else if (nextToken.startsWith('narrative')) {
		   	   hierarchy.add('Narrative' + nextToken.substring(nextToken.lastIndexOf('=') + 1))
           } else if (nextToken.startsWith('poem')) {
		       hierarchy.add('Poem ' + nextToken.substring(nextToken.lastIndexOf('=') + 1))
		   }
	   }
	   	   
	   return new Hierarchy(hierarchy)
   }

}
