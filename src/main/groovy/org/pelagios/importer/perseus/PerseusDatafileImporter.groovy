package org.pelagios.importer.perseus

import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.builder.DataRecordBuilder;
import org.pelagios.backend.graph.builder.DatasetBuilder;
import org.pelagios.backend.graph.exception.DatasetExistsException;
import org.pelagios.importer.AbstractDatasetImporter;
import org.pelagios.importer.Hierarchy;

import com.hp.hpl.jena.rdf.model.Resource

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
		
		HashMap<Hierarchy, List<DataRecordBuilder>> allRecords = 
			new HashMap<Hierarchy, List<DataRecordBuilder>>()
		
		for (Resource oac : listOACAnnotations()) {			
			// Annotation URI for building the hierarchy
			Hierarchy h = getHierarchy(oac.toString())
			
			// Target = data record URN
			String recordURL = oac.getProperty(OAC_HASTARGET).getObject().toString()
			
			// Body = Pleiades Place
			String pleiadesURL = oac.getProperty(OAC_HASBODY).getObject().toString()

			// Create the record and store in memory - we'll batch-add 
			// all records to the graph later for added performance
			try {
				List<DataRecordBuilder> records = allRecords.get(h)
				if (records == null) {
					records = new ArrayList<DataRecordBuilder>()
					allRecords.put(h, records)
				}
				DataRecordBuilder record =
					new DataRecordBuilder(new URI(URLEncoder.encode(recordURL, 'UTF-8')))
				record.addPlaceReference(new URI(pleiadesURL))
				records.add(record)
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
	   String[] s = tokenizer.nextToken().split("\\.")
	   for (int i=0; i<s.length; i++) {
	       hierarchy.add(s[i])
	   }
	   
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
