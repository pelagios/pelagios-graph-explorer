package org.pelagios.explorer;

import org.pelagios.graph.builder.PelagiosGraphImpl;
import org.pelagios.graph.builder.PelagiosGraphBuilder;

public class Backend {
	
	private static final String DATA_DIR = Config.getInstance().getNeo4jDirectory();
	
	private static PelagiosGraphImpl instance = null;
	
	public static PelagiosGraphImpl getInstance() {
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
