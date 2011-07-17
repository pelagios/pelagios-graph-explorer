package org.pelagios.graph.importer.pleiades.locations;

import java.util.Date;

import org.pelagios.api.GeoJSONGeometry;

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
	
	private GeoJSONGeometry geoJson;

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

	public void setGeoJSON(GeoJSONGeometry geometry) {
		this.geoJson = geometry;
	}

	public GeoJSONGeometry getGeoJSON() {
		return geoJson;
	}

}
