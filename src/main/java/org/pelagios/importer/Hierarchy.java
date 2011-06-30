package org.pelagios.importer;

import java.util.List;

public class Hierarchy {
	
	private List<String> levels;
	
	public Hierarchy(List<String> levels) {
		this.levels = levels;
	}
	
	public List<String> getLevels() {
		return levels;
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
