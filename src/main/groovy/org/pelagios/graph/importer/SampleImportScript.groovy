import java.io.File

import org.pelagios.explorer.Config
import org.pelagios.graph.PelagiosGraphAdmin;
import org.pelagios.graph.importer.nomisma.NomismaDatasetImporter
import org.pelagios.graph.importer.pleiades.PleiadesDumpFiles

// A sample Groovy script that imports Pleiades and some sample data
def initStart = System.currentTimeMillis()

def neo4j = new PelagiosGraphAdmin()

def taskstart, duration

println("Dropping existing database")
neo4j.clearDatabase()

taskStart = System.currentTimeMillis()
println("Importing Pleiades Gazetteer")
def pleiades = new PleiadesDumpFiles(new File("data"))
neo4j.importPleiades(pleiades.getLocationsCSV(), pleiades.getNamesCSV())
duration = System.currentTimeMillis() - taskStart
println("Done. (${duration} ms)")

taskStart = System.currentTimeMillis()
println("Importing nomisma dataset")
neo4j.importDataset(new NomismaDatasetImporter(
	new File("data/datasets/sample-nomisma.org.rdf")))
duration = System.currentTimeMillis() - taskStart
println("Done. (${duration} ms)")
		
neo4j.shutdown()
duration = (System.currentTimeMillis() - initStart) / 1000
println("Database initialized. Took ${duration} seconds.");

