package org.pelagios.pleiades.locations;

import java.util.Date;


/**
 * The data contained in one line of the 'pleiades-locations' 
 * CSV dump.
 * 
 * @author Rainer Simon
 */
public class LocationRecord {

	private String pid;
	
	private Date created;
	
	private String creators;
	
	private String description;
	
	private Geometry geometry;

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPid() {
		return pid;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreators(String creators) {
		this.creators = creators;
	}

	public String getCreators() {
		return creators;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public Geometry getGeometry() {
		return geometry;
	}

}
