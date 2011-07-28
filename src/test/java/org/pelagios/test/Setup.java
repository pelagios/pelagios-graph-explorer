package org.pelagios.test;

import java.io.File;

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.builder.PelagiosGraphBuilder;

public class Setup {
	
	private static final String TEST_GRAPH_DIR = "neo4j-unit-test";
	
	private static PelagiosGraph testGraph = null;

	static {
		System.out.print("Clearing existing test database (if any)");
		delete(new File(TEST_GRAPH_DIR));
		System.out.println(" - done");
		
		System.out.print("Creating new test database");
		testGraph = new PelagiosGraphBuilder(TEST_GRAPH_DIR).build();
		System.out.println(" - done");
	}
	
	public static PelagiosGraph getTestGraph() {
		return testGraph;
	}
			
	private static void delete(File file) {
	    if (file.exists()) {
	        if (file.isDirectory() ) {
	            for (File child : file.listFiles()) {
	            	delete(child);
	            }
	        }
	        file.delete();
	    }
	}
	
}
