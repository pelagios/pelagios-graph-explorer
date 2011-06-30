package org.pelagios.importer;

import java.util.List;

public class FlexHierarchy {
	
	private List<String> levels;
	
	public FlexHierarchy(List<String> levels) {
		this.levels = levels;
	}
	
	public List<String> getLevels() {
		return levels;
	}
	
	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof FlexHierarchy))
			return false;
		
		FlexHierarchy other = (FlexHierarchy) arg;
		if (this.levels.size() != other.levels.size())
			return false;
		
		for (int i=0; i<levels.size(); i++) {
			if (!this.levels.get(i).equals(other.levels.get(i)))
				return false;
		}
		
		return true;
	}

}
