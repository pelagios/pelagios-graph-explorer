package org.pelagios.importer.nomisma

import java.io.File
import java.net.URI
import java.net.URISyntaxException
import java.util.ArrayList
import java.util.List

import org.pelagios.graph.PelagiosGraph
import org.pelagios.graph.builder.DataRecordBuilder
import org.pelagios.graph.builder.DatasetBuilder
import org.pelagios.graph.exception.DatasetExistsException
import org.pelagios.graph.exception.DatasetNotFoundException
import org.pelagios.importer.AbstractDatasetImporter

import com.hp.hpl.jena.rdf.model.Resource

/**
 * Importer class for the nomisma data set.
 * 
 * @author Rainer Simon
 */
public class NomismaDatasetImporter extends AbstractDatasetImporter {

	public NomismaDatasetImporter(File rdf) {
		super(rdf, new DatasetBuilder("nomisma"))
	}

	@Override
	public void importData(PelagiosGraph graph) throws DatasetExistsException {
		graph.addDataset(rootNode)
		
		List<DataRecordBuilder> records = new ArrayList<DataRecordBuilder>()
		for (Resource oac : listOACAnnotations()) {
			DataRecordBuilder record = new DataRecordBuilder(
					new URI(oac.getProperty(OAC_HASTARGET).getObject().toString()))
			record.addPlaceReference(
					new URI(oac.getProperty(OAC_HASBODY).getObject().toString()))
			records.add(record);
		}
		
		graph.addDataRecords(records, rootNode)
	}

}
