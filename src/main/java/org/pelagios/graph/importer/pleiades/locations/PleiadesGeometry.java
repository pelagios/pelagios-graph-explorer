package org.pelagios.graph.importer.pleiades.locations;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A simple class that wraps the information contained in the 'geometry'
 * field of the 'pleiades-locations' CSV dump. That's (a) the 'relation'
 * ('rough' or 'precise') and (b) the actual GEOJSON geometry. 
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
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
