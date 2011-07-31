import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.importer.gap.GAPImporter;

println("Starting GAP import")
def graph = PelagiosGraph.getDefaultDB()
def gap = new GAPImporter(new File("data/datasets/GAPtriples.n3"))
gap.importData(graph)
graph.shutdown()
println("Done.")
