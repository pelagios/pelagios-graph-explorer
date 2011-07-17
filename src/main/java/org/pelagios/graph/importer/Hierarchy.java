package org.pelagios.graph.importer;

import java.util.ArrayList;
import java.util.List;

public class Hierarchy {
	
	private List<String> levels;
	
	public Hierarchy(List<String> levels) {
		this.levels = levels;
	}
	
	public List<String> getLevels() {
		List<String> qNames = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		for (String level : levels) {
			qNames.add(sb.toString() + level);
			sb.append(level + ":");
		}
		return qNames;
	}
	
	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof Hierarchy))
			return false;
		
		Hierarchy other = (Hierarchy) arg;
		if (this.levels.size() != other.levels.size())
			return false;
		
		for (int i=0; i<levels.size(); i++) {
			if (!this.levels.get(i).equals(other.levels.get(i)))
				return false;
		}
		
		return true;
	}

}
