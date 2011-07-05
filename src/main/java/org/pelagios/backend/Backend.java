package org.pelagios.backend;

import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.builder.PelagiosGraphBuilder;

public class Backend {
	
	// TODO make configurable (via servlet context param)
	private static final String DATA_DIR = "c:/neo4j-data";
	
	private static PelagiosGraph instance = null;
	
	public static PelagiosGraph getInstance() {
		if (instance == null) {
			PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(DATA_DIR);
			instance = graphBuilder.build();
		}
		return instance;
	}
	
	public static void shutdown() {
		if (instance != null)
			instance.shutdown();
	}

}
