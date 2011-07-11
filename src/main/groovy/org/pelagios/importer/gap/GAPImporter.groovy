package org.pelagios.importer.gap

import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.builder.DataRecordBuilder;
import org.pelagios.backend.graph.builder.DatasetBuilder;
import org.pelagios.backend.graph.exception.DatasetExistsException;
import org.pelagios.importer.AbstractDatasetImporter;
import org.pelagios.importer.Hierarchy;

import com.hp.hpl.jena.rdf.model.Resource

/**
 * Importer for the GAP data file. N3 Triple sampe:
 * 
 * <http://gap.alexandriaarchive.org/bookdata/GAPtriples.n3#annotation1>
 *    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
 *    <http://www.openannotation.org/ns/Annotation> .
 * 
 * <http://gap.alexandriaarchive.org/bookdata/GAPtriples.n3#annotation1> 
 *    <http://www.openannotation.org/ns/hasBody>
 *    <http://pleiades.stoa.org/places/766> .
 *  
 * <http://gap.alexandriaarchive.org/bookdata/GAPtriples.n3#annotation1>
 *    <http://www.openannotation.org/ns/hasTarget>
 *    <http://www.google.com/books?id=-C0BAAAAQAAJ&pg=PA17#bbox=390,1496,472,1525> .
 * 
 * To introduce some structuring in the GAP data, we group by book IDs, 
 * e.g. -C0BAAAAQAAJ in the above sample.
 * 
 * @author Rainer Simon
 */
class GAPImporter extends AbstractDatasetImporter {
	
	public GAPImporter(File rdf) {
		super(rdf, new DatasetBuilder("Google Ancient Places"), "N3")
	}

	/**
	 * Imports the RDF data into the Pelagios Graph.
	 * @param graph the graph
	 * @throws DatasetExistsException 
	 * @throws PlaceNotFoundException 
	 */
	@Override
	public void importData(PelagiosGraph graph) throws DatasetExistsException {
		graph.addDataset(rootNode)
		
		HashMap<Hierarchy, List<DataRecordBuilder>> allRecords = 
			new HashMap<Hierarchy, List<DataRecordBuilder>>()
		
		for (Resource oac : listOACAnnotations()) {			
			// Target = data record URN
			String target = oac.getProperty(OAC_HASTARGET).getObject().toString()
			
			// Body = Pleiades Place
			String body = oac.getProperty(OAC_HASBODY).getObject().toString()
			
			// 'Hierarchy' based on the target URI
			Hierarchy h = getHierarchy(target)

			// Create the record and store in memory - we'll batch-add 
			// all records to the graph later for added performance
			try {
				List<DataRecordBuilder> records = allRecords.get(h)
				if (records == null) {
					records = new ArrayList<DataRecordBuilder>()
					allRecords.put(h, records)
				}
				DataRecordBuilder record =
					new DataRecordBuilder(new URI(URLEncoder.encode(target, 'UTF-8')))
				record.addPlaceReference(new URI(body))
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
	 * The 'hierarchy' in the GAP case is one level only - the book ID
	 * @param uri the data record URI
	 * @return the hierarchy
	 */
	Hierarchy getHierarchy(String uri) {
	   List<String> hierarchy = new ArrayList<String>()
	   
	   int idIdx = uri.indexOf('id=')
	   if (idIdx > -1) {
		   idIdx += 3
		   int toIdx = uri.indexOf('&', idIdx)
		   hierarchy.add('GAP:' + uri.substring(idIdx, toIdx))
	   }
	     
	   return new Hierarchy(hierarchy)
   }

}
