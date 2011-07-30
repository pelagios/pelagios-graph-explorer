package org.pelagios.graph.importer.spqr

import com.hp.hpl.jena.rdf.model.ResourceFactory;

import java.awt.Label;
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
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DC;
import com.sun.jmx.mbeanserver.MXBeanProxy.GetHandler;

import org.apache.log4j.Logger
import org.apache.xerces.dom.ParentNode;
import org.pelagios.explorer.rest.DatasetController;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.GeoAnnotationBuilder;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.importer.AbstractDatasetImporter
import org.pelagios.graph.importer.Hierarchy;
import org.pelagios.graph.nodes.GeoAnnotation;

class SPQRImporter extends AbstractDatasetImporter {
	
	private static final String OAC_NAMESPACE = "http://www.openannotation.org/ns/"
	private static final String SPQR_NAMESPACE = "http://spqr.epcc.ed.ac.uk/";
	
	private File downloadDir;
	
	private Logger log = Logger.getLogger(SPQRImporter.class)
	
	public SPQRImporter(File downloadDir) {
		super();
		this.downloadDir = downloadDir;
	}
	
	public void importData(PelagiosGraph graph) {				
		HashMap<Hierarchy, List<GeoAnnotationBuilder>> allRecords =
			new HashMap<Hierarchy, List<GeoAnnotationBuilder>>()
		
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
				
				String label = (a.getLabel() == null) ? a.getTarget().toString() : a.getLabel()
				record.setLabel(label)
				
				if (a.getMaterial() != null)
					record.addProperty("Material", a.getMaterial())
				
				if (a.getType() != null)
					record.addProperty("Type", a.getType())
				
				if (a.getBody().toString().indexOf("pleiades") > -1) {
					record.addPlaceReference(a.getBody())
					uniqueUris.add(a.getBody());
				}
			}
	
			Hierarchy h = getHierarchy(file)
			List<GeoAnnotationBuilder> aList= allRecords.get(h)
			if (aList == null) {
				aList = new ArrayList<GeoAnnotationBuilder>()
			}
			aList.add(record)
			allRecords.put(h, aList);
		}
		
		batchAdd(allRecords, graph);
	}
	
	Hierarchy getHierarchy(File file) {
		List<String> hierarchy = new ArrayList<String>()
		hierarchy.add("SPQR")
		
		String name = file.getName()
		if (name.contains("hgv")) {
			hierarchy.add("HGV Papyri")
		} else if (name.contains("IRT")) {
			hierarchy.add("Inscriptions of Roman Tripolitania")
		} else {
			hierarchy.add("Inscriptions of Aphrodisias")
		}	
		
		return new Hierarchy(hierarchy)
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

		public String getName() {
			return resource.getProperty(ResourceFactory.createProperty(SPQR_NAMESPACE, "name")).getObject().toString()
		}
				
		public String getLabel() {
			List<Resource> parent = getParentNode();
			if (parent.size() > 0) {
				Statement s = parent.get(0).getProperty(DC.title);
				if (s != null)
					return s.getObject().toString()	
			}
			
			String name = getName();
			if (name.isEmpty())
				return "...";
			
			return name;
		}

		public String getMaterial() {
			List<Resource> parent = getParentNode();
			if (parent.size() > 0) {
				return parent.get(0).getProperty(ResourceFactory.createProperty(SPQR_NAMESPACE, "material")).getObject().toString()
			}
		}
		
		public String getType() {
			return resource.getProperty(ResourceFactory.createProperty(SPQR_NAMESPACE, "type")).getObject().toString()
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
		
		private List<Resource> getParentNode() {
			return resource.getModel().listResourcesWithProperty(ResourceFactory.createProperty(SPQR_NAMESPACE, "material")).toList();
		}
		
	}

}
