package org.pelagios.geo.shape;

import org.junit.Assert;
import org.junit.Test;

public class WKTTest {
	
	@Test
	public void testLineStringSerialization() {
		LineString linestring = new LineString();
		linestring.addPoint(new Point(0, 0, 0));
		linestring.addPoint(new Point(1, 2, 3));
		linestring.addPoint(new Point(10, 10, 10));
		linestring.addPoint(new Point(20, 10, 0));
		
		Assert.assertEquals("LINESTRING (0.0 0.0 0.0, 2.0 1.0 3.0, 10.0 10.0 10.0, 10.0 20.0 0.0)", linestring.toWKT());
	}
	
	@Test
	public void testPolygonSerialization() {
		LineString linestring = new LineString();
		linestring.addPoint(new Point(0, 0, 0));
		linestring.addPoint(new Point(1, 2, 3));
		linestring.addPoint(new Point(10, 10, 10));
		linestring.addPoint(new Point(20, 10, 0));
		Polygon polygon = new Polygon(linestring);
		
		Assert.assertEquals("POLYGON ((0.0 0.0 0.0, 2.0 1.0 3.0, 10.0 10.0 10.0, 10.0 20.0 0.0))", polygon.toWKT());
	}

}