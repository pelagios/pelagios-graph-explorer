import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.importer.perseus.PerseusImporter;

println("Starting Perseus import")
def graph = PelagiosGraph.getDefaultDB()
HashMap<String, File> files = new HashMap<String, File>()
files.put("Perseus Greco-Roman", new File("data/datasets/perseus-greco-roman.rdf"));
files.put("Perseus Renaissance", new File("data/datasets/perseus-renaissance.rdf"));
files.put("Perseus Richmond Times", new File("data/datasets/perseus-richmond-times.rdf"));

def perseus = new PerseusImporter(files)
perseus.importData(graph)
graph.shutdown()
println("Done.")
