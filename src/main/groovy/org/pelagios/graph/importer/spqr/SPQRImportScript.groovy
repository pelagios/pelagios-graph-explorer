import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.importer.spqr.SPQRImporter;

println("Starting SPQR import")
def graph = PelagiosGraph.getDefaultDB()
def spqr = new SPQRImporter(new File("data/datasets/spqr/downloads"))
spqr.importData(graph)
graph.shutdown()
println("Done.")
