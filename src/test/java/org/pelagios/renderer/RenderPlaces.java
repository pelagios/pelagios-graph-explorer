package org.pelagios.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.geotools.geometry.DirectPosition2D;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.Place;
import org.pelagios.graph.builder.PelagiosGraphBuilder;
import org.pelagios.rendering.ShapefileRenderer;

/**
 * A utility class that renders the places contained in
 * the Pelagios graph to an image. Points that cover more
 * than one place are drawn in lighter color.
 * 
 * TODO turn this into a decent implementation
 * 
 * @author Rainer Simon
 */
public class RenderPlaces {
	
	/**
	 * Graph DB data directory
	 */
	private static final String DATA_DIR = "c:/neo4j-data";
	
	/**
	 * World map shapefile
	 */
	private static final String SHAPEFILE = "src\\test\\resources\\110m-coastline\\110m_coastline.shp";
	
	/**
	 * Result file
	 */
	private static final String OUTPUT_FILE = "rendered-map.png";
	
	private static final HashMap<Point, Integer> points =
		new HashMap<Point, Integer>();
	
	/**
	 * IMPORTANT: This application assumes the graph DB is already 
	 * populated with Pleiades places.
	 * 
	 * @param args no arguments needed
	 * @throws IOException if anything goes wrong while saving the image
	 * @throws TransformException 
	 * @throws MismatchedDimensionException 
	 */
	public static void main(String[] args)
		throws IOException, MismatchedDimensionException, TransformException {
		
		// Create a 640x400 pixel drawing canvas
		Rectangle paintArea = new Rectangle(0, 0, 640, 400);
		BufferedImage bg = new BufferedImage(
				(int) paintArea.getWidth(),
				(int) paintArea.getHeight(), 
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bg.createGraphics();
		
		// Render the world map shapefile
		ShapefileRenderer renderer = new ShapefileRenderer(new File(SHAPEFILE));
		MathTransform transform = renderer.renderShapefile(g2d, paintArea);
		
		// Get a handle on the Pelagios Graph
		PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(DATA_DIR);
		PelagiosGraph graph = graphBuilder.build();

		// Add places to the map image
		int max = 1;
		for (Place p : graph.listPlaces()) {
			Point xy = transform(p.getLon(), p.getLat(), transform);
			if (points.containsKey(xy)) {
				Integer count = points.get(xy);
				count++;
				points.put(xy, count);
				if (count > max)
					max = count;
			} else {
				points.put(xy, new Integer(1));
			}
		}
		System.out.println(points.size() + " points on screen");
		System.out.println(max + " maximum density");
		
		double delta = 160 / 30;
		for (Point p : points.keySet()) {
			int brightness = 95 + (int) (points.get(p) * delta);
			if (brightness > 255)
				brightness = 255;
			int rg = (int) (brightness * 0.5);
			g2d.setPaint(new Color(brightness, rg, rg));
			g2d.fillOval(p.x, p.y, 1, 1);
		}
		
		graph.shutdown();
		ImageIO.write(bg, "png", new File(OUTPUT_FILE));  
	}

	private static Point transform(double lon, double lat, MathTransform transform)
		throws MismatchedDimensionException, TransformException {
		
		DirectPosition src = new DirectPosition2D(lon, lat);
		DirectPosition dst = new DirectPosition2D(); 
		dst = transform.transform(src, dst);
		
		return new Point((int) dst.getCoordinate()[0], (int) dst.getCoordinate()[1]);
	}
	
}
