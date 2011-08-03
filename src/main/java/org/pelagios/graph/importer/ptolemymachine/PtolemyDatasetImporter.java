package org.pelagios.graph.importer.ptolemymachine;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.GeoAnnotationBuilder;
import org.pelagios.graph.builder.DatasetBuilder;
import org.pelagios.graph.exceptions.DatasetExistsException;
import org.pelagios.graph.importer.AbstractDatasetImporter;
import org.pelagios.graph.importer.Hierarchy;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Importer for the Ptolemy Machine dataset. RDF/XML sample:
 * 
 * <oac:Annotation
 *   rdf:about="http://shot.holycross.edu:8080/ptolgeoinv/id/pt-ll-1">
 *   <oac:hasBody> 
 *     <rdf:Description rdf:about="urn:cite:ptolemymachine:pt-ll-1">
 *       <dcterms:source
 *         rdf:resource="http://shot.holycross.edu:8080/ptolgeoinv/id/pt-ll-1"/>
 *     </rdf:Description> 
 *   </oac:hasBody>
 *   <oac:hasTarget>
 *     <rdf:Description rdf:about="urn:cts:greekLit:tlg0363.tlg009.chs01:2.2.1:">
 *       <dcterms:isPartOf rdf:resource="urn:cts:greekLit:tlg0363.tlg009.chs01"/>
 *       <dcterms:source rdf:resource= "..." />
 *     </rdf:Description>
 *   </oac:hasTarget>
 *   <dc:title xml:lang="en">Ptolemy's Geography: Boreion akron</dc:title>
 *   <dcterms:creator>Neel Smith</dcterms:creator> 
 *   <dcterms:created>2011-05-01</dcterms:created>
 * </oac:Annotation>
 * 
 * To introduce some structuring in the Ptolemy Machine data, I defined a two
 * level hierarchy, based on the URN format:
 * 
 * urn:cts:greekLit:tlg0363.tlg009.chs01:2.2.1:
 * 
 * Level one is all annotations with same N: [...]tlg009.chs01:N.x.x
 * 
 * Level two is all annotations with same N and M: [...]tlg009.chs01:N.M.x
 * 
 * TODO Ptolemy Machine data is NOT aligned with Pleiades -> find a way to deal with this
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class PtolemyDatasetImporter extends AbstractDatasetImporter {

    public PtolemyDatasetImporter(File rdf) {
        super(rdf, new DatasetBuilder("Ptolemy Machine"));
    }

    @Override
    public void importData(PelagiosGraph graph) throws DatasetExistsException {
        // Start by creating the root node
        graph.addDataset(rootNode);

        HashMap<Hierarchy, List<GeoAnnotationBuilder>> allRecords = new HashMap<Hierarchy, List<GeoAnnotationBuilder>>();

        for (Resource oac : listOACAnnotations()) {
            // Annotation title ... place name
            String title = oac.getProperty(DC.title).getObject().toString();

            // Target description = data record URN
            RDFNode target = oac.getProperty(OAC_HASTARGET).getObject();
            String targetURN = target.toString();

            // Target DCTerms source = data record URL
            String targetURL = target.asResource().getProperty(DCTerms.source).getObject().toString();

            // Create the record and store in memory - we'll batch-add
            // all records to the graph later for added performance
            Hierarchy h = getHierarchy(targetURN);
            try {
                List<GeoAnnotationBuilder> records = allRecords.get(h);
                if (records == null) {
                    records = new ArrayList<GeoAnnotationBuilder>();
                    allRecords.put(h, records);
                }
                
                GeoAnnotationBuilder annotation = 
                    new GeoAnnotationBuilder(new URI(targetURL), new URI("http://pelagios.org/null"));
                
                annotation.setLabel(title);
                records.add(annotation);
            } catch (URISyntaxException e) {
                // Only happens in case of data set errors
                throw new RuntimeException(e);
            }
        }

        batchAdd(allRecords, graph);
    }

    Hierarchy getHierarchy(String urn) {
        StringTokenizer tokenizer = new StringTokenizer(urn, ":");

        if (tokenizer.countTokens() < 5)
            throw new RuntimeException("Illegal URN format - looks like an error in the dataset: " + urn);

        // Skip the first 4 tokens
        for (int i = 0; i < 4; i++)
            tokenizer.nextToken();

        String s[] = tokenizer.nextToken().split("\\.");
        if (s.length < 2)
            throw new RuntimeException("Illegal URN format - looks like an error in the dataset: " + urn);

        List<String> hierarchy = new ArrayList<String>();
        hierarchy.add("Ptolemy Machine " + s[0]);
        hierarchy.add(s[1]);
        return new Hierarchy(hierarchy);
    }

}
