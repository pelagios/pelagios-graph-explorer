import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.importer.ptolemymachine.PtolemyDatasetImporter;

println("Starting Ptolemy Machine import")
def graph = PelagiosGraph.getDefaultDB()
def ptolemy = new PtolemyDatasetImporter(new File("data/datasets/ptolemy.rdf"))
ptolemy .importData(graph)
graph.shutdown()
println("Done.")
