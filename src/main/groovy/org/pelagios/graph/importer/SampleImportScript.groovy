import java.io.File

import org.pelagios.explorer.Config
import org.pelagios.graph.PelagiosGraph
import org.pelagios.graph.importer.nomisma.NomismaDatasetImporter
import org.pelagios.graph.importer.pleiades.PleiadesDumpFiles
import org.pelagios.graph.importer.pleiades.PleiadesImporter;

// A sample Groovy script that imports Pleiades and some sample data
def initStart = System.currentTimeMillis()

def graph = PelagiosGraph.getDefaultDB()

def taskstart, duration

taskStart = System.currentTimeMillis()
println("Importing Pleiades Gazetteer")
def pleiades = new PleiadesDumpFiles(new File("data"))
def places = new PleiadesImporter()
	.importPleiadesDump(pleiades.getLocationsCSV(), pleiades.getNamesCSV())
graph.addPlaces(places)
duration = System.currentTimeMillis() - taskStart
println("Done. (${duration} ms)")

taskStart = System.currentTimeMillis()
println("Importing nomisma dataset")
def nomisma = new NomismaDatasetImporter(new File("data/datasets/sample-nomisma.org.rdf"))
nomisma.importData(graph)
duration = System.currentTimeMillis() - taskStart
println("Done. (${duration} ms)")
		
graph.shutdown()
duration = (System.currentTimeMillis() - initStart) / 1000
println("Database initialized. Took ${duration} seconds.");

