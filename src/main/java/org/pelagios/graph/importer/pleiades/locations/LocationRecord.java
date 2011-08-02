package org.pelagios.graph.importer.pleiades.locations;

import java.util.Date;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A simple class that wraps the (relevant) data contained in one
 * line of the 'pleiades-locations' CSV dump.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class LocationRecord {

    private String pid;

    private Date created;

    private String creators;

    private String description;

    private String relation;

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

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getRelation() {
        return relation;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Geometry getGeometry() {
        return geometry;
    }

}
