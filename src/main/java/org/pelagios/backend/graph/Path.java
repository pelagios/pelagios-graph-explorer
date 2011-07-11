package org.pelagios.backend.graph;

import java.util.List;

public class Path {
	
	private List<Object> entities;
	
	public Path(List<Object> entities) {
		this.entities = entities;
	}
	
	public List<Object> getEntities() {
		return entities;
	}

}
