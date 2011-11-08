package org.pelagios.graph.importer.arachne

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.builder.GeoAnnotationBuilder;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.importer.AbstractDatasetImporter;

import com.hp.hpl.jena.rdf.model.Resource;

class ArachneImporter extends AbstractDatasetImporter {
    
    public ArachneImporter(File rdf) {
        super(rdf, new DatasetBuilder("Arachne"), "N3")
    }
    
    @Override
    public void importData(PelagiosGraph graph) throws DatasetExistsException {
        try {
            graph.addDataset(rootNode)
        } catch (Exception e) {
            println("Appending to existing Arachne root node")
        }
        
        List<GeoAnnotationBuilder> records = new ArrayList<GeoAnnotationBuilder>()
        for (Resource oac : listOACAnnotations()) {
            String target = oac.getProperty(OAC_HASTARGET).getObject().toString()
            
            GeoAnnotationBuilder annotation = new GeoAnnotationBuilder(
                new URI(target),
                new URI(oac.getProperty(OAC_HASBODY).getObject().toString()))
            
            annotation.setLabel(target)
            records.add(annotation);
        }
        
        graph.addGeoAnnotations(records, rootNode)
    }
    
}
