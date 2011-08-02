package org.pelagios.io.geojson;

public enum GeometryType {

    POINT {
        public String toString() {
            return "Point";
        }
    },

    LINESTRING {
        public String toString() {
            return "LineString";
        }
    },

    POLYGON {
        public String toString() {
            return "Polygon";
        }
    }

}
