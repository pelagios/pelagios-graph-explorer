package org.pelagios.graph.importer.pleiades.locations;

import com.vividsolutions.jts.geom.Geometry;

public class PleiadesGeometry {
	
	private String relation;
	
	private Geometry geometry;
	
	public String getRelation() {
		return relation;
	}
	
	public void setRelation(String relation) {
		this.relation = relation;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	
}
