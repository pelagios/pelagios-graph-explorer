package org.pelagios.graph;

import java.util.List;

public class Path {
	
	private List<PelagiosGraphNode> nodes;
	
	public Path(List<PelagiosGraphNode> nodes) {
		this.nodes = nodes;
	}
	
	public List<PelagiosGraphNode> getEntities() {
		return nodes;
	}

}
