package org.pelagios.graph.importer.spqr

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
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.GeoAnnotationBuilder;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.importer.AbstractDatasetImporter;
import org.pelagios.graph.importer.Hierarchy;

class SPQRImporter {
	
	private static final String OAC_NAMESPACE = "http://www.openannotation.org/ns/"
	
	private Logger log = Logger.getLogger(SPQRImporter.class)
	
	public SPQRImporter(File downloadDir, PelagiosGraph graph) {
		List<GeoAnnotationBuilder> records = new ArrayList<GeoAnnotationBuilder>();
		
		
		Set<URI> uniqueUris = new HashSet<URI>(); 
		
		DatasetBuilder rootNode = new DatasetBuilder("SPQR")
		graph.addDataset(rootNode)
		
		downloadDir.eachFile { file ->
			Model model = ModelFactory.createDefaultModel()
			model.read(new FileInputStream(file), null)
			
			List<Resource> annotations = model
				.listResourcesWithProperty(model.createProperty(OAC_NAMESPACE, "hasBody"))
				.toList()
				
			GeoAnnotationBuilder record = new GeoAnnotationBuilder()
			
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
