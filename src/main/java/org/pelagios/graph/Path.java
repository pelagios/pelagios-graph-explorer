package org.pelagios.graph;

import java.util.List;

import org.pelagios.graph.PelagiosGraphNode.NodeType;
import org.pelagios.graph.nodes.Dataset;
import org.pelagios.graph.nodes.Place;

public class Path {
	
	private List<PelagiosGraphNode> nodes;
	
	public Path(List<PelagiosGraphNode> nodes) {
		this.nodes = nodes;
	}
	
	public List<PelagiosGraphNode> getNodes() {
		return nodes;
	}
	
	public Path shrink() {
		if (nodes.size() < 5)
			return this;
		
		nodes.remove(nodes.size() - 2);
		nodes.remove(1);
		return this;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (PelagiosGraphNode n : nodes) {
			sb.append(n.toString() + " - ");
		}
		return sb.substring(0, sb.length() - 3).toString();
	}
	
	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof Path))
			return false;
		
		Path other = (Path) arg;
		if (other.nodes.size() != this.nodes.size())
			return false;
		
		for (int i=0; i<this.nodes.size(); i++) {
			if (!this.nodes.get(i).equals(other.nodes.get(i)))
				return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		StringBuffer sb = new StringBuffer();
		for (PelagiosGraphNode n : nodes) {
			if (n.getType() == NodeType.PLACE) {
				sb.append(((Place) n).getURI().toString());
			} else if (n.getType() == NodeType.DATASET) {
				sb.append(((Dataset) n).getName());
			}
		}
		return sb.toString().hashCode();
	}

}
