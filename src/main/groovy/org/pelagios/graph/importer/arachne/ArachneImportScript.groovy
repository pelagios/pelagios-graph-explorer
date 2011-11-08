package org.pelagios.graph.importer.arachne

import org.pelagios.graph.PelagiosGraph;

println("Starting Arachne import")
def graph = PelagiosGraph.getDefaultDB()

def arachne = new ArachneImporter(new File("data/datasets/Arachne/PleiadesRDFObjektbyPlace.n3"))
arachne.importData(graph)

graph.shutdown()
println("Done.")
