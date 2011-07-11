package org.pelagios.importer.spqr

import com.hp.hpl.jena.rdf.model.ResourceFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource

import org.apache.log4j.Logger
import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.builder.DataRecordBuilder
import org.pelagios.backend.graph.builder.DatasetBuilder
import org.pelagios.backend.graph.exception.DatasetExistsException;
import org.pelagios.importer.AbstractDatasetImporter;
import org.pelagios.importer.Hierarchy

class SPQRImporter {
	
	private static final String OAC_NAMESPACE = "http://www.openannotation.org/ns/"
	
	private Logger log = Logger.getLogger(SPQRImporter.class)
	
	public SPQRImporter(File downloadDir, PelagiosGraph graph) {
		List<DataRecordBuilder> records = new ArrayList<DataRecordBuilder>();
		
		
		Set<URI> uniqueUris = new HashSet<URI>(); 
		
		DatasetBuilder rootNode = new DatasetBuilder("SQPR")
		graph.addDataset(rootNode)
		
		downloadDir.eachFile { file ->
			Model model = ModelFactory.createDefaultModel()
			model.read(new FileInputStream(file), null)
			
			List<Resource> annotations = model
				.listResourcesWithProperty(model.createProperty(OAC_NAMESPACE, "hasBody"))
				.toList()
				
			DataRecordBuilder record = new DataRecordBuilder()
			
			for (Resource r : annotations) {
				PelagiosAnnotation a = new PelagiosAnnotation(r)
				record.setDataURL(a.getTarget())
				if (a.getBody().toString().indexOf("pleiades") > -1) {
					record.addPlaceReference(a.getBody())
					uniqueUris.add(a.getBody());
				}
			}
			
			records.add(record);
		}
		
		graph.addDataRecords(records, rootNode)
	}
	
	class PelagiosAnnotation {
		
		private Resource resource;
	
		public PelagiosAnnotation(Resource resource) {
			this.resource = resource;
			
		}
		
		public URI getBody() {
			return get(ResourceFactory.createProperty(OAC_NAMESPACE, "hasBody"));		
		}
		
		public URI getTarget() {
			return get(ResourceFactory.createProperty(OAC_NAMESPACE, "hasTarget"));
		}
		
		private URI get(Property p) {
			RDFNode node = resource.getProperty(p).getObject();
			try {
				String uri = node.toString()
				if (uri.endsWith("/"))
					uri = uri.substring(0, uri.length()  - 1)
					
				return new URI(uri);
			} catch (URISyntaxException e) {
				// This should never ever happen
				throw new RuntimeException(e);
			}
		}
		
	}

}
