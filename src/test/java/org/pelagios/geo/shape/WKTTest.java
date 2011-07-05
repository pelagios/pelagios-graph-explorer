package org.pelagios.geo.shape;

import org.junit.Assert;
import org.junit.Test;
import org.pelagios.backend.geo.shape.LineString;
import org.pelagios.backend.geo.shape.Polygon;

import com.vividsolutions.jts.geom.Coordinate;

public class WKTTest {
	
	@Test
	public void testLineStringSerialization() {
		LineString linestring = new LineString();
		linestring.addCoordinate(new Coordinate(0, 0, 0));
		linestring.addCoordinate(new Coordinate(1, 2, 3));
		linestring.addCoordinate(new Coordinate(10, 10, 10));
		linestring.addCoordinate(new Coordinate(20, 10, 0));
		
		Assert.assertEquals("LINESTRING (0.0 0.0 0.0, 1.0 2.0 3.0, 10.0 10.0 10.0, 20.0 10.0 0.0)", linestring.toWKT());
	}
	
	@Test
	public void testPolygonSerialization() {
		LineString linestring = new LineString();
		linestring.addCoordinate(new Coordinate(0, 0, 0));
		linestring.addCoordinate(new Coordinate(1, 2, 3));
		linestring.addCoordinate(new Coordinate(10, 10, 10));
		linestring.addCoordinate(new Coordinate(20, 10, 0));
		Polygon polygon = new Polygon(linestring);
		
		Assert.assertEquals("POLYGON ((0.0 0.0 0.0, 1.0 2.0 3.0, 10.0 10.0 10.0, 20.0 10.0 0.0))", polygon.toWKT());
	}

}
