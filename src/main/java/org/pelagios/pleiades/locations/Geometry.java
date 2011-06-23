package org.pelagios.pleiades.locations;

import org.pelagios.pleiades.locations.shape.Shape;


/**
 * Class representation of the Pleiades 'Geometry' data structure
 * 
 * @author Rainer Simon
 */
public class Geometry {
	
	public enum Relation { ROUGH , PRECISE }
	
	public enum Type { POINT , POLYGON, LINESTRING }
	
	private Relation relation;
	
	private Type type;
	
	private Shape shape;
	
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
	
	public Relation getRelation() {
		return relation;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setShape(Shape shape) {
		this.shape = shape;
	}
	
	public Shape getShape() {
		return shape;
	}
	
}
