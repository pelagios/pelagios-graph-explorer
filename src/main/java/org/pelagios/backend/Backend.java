package org.pelagios.backend;

import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.builder.PelagiosGraphBuilder;

public class Backend {
	
	private static final String DATA_DIR = Config.getInstance().getNeo4jDirectory();
	
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
