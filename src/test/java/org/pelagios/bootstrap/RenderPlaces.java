package org.pelagios.bootstrap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.Place;
import org.pelagios.graph.builder.PelagiosGraphBuilder;

/**
 * A utility class that renders the places contained in
 * the Pelagios graph to an image. Points that cover more
 * than one place are drawn in lighter color.
 * 
 * TODO implement this
 * 
 * TODO turn this mess into a decent implementation
 * 
 * @author Rainer Simon
 */
public class RenderPlaces {
	
	private static final String DATA_DIR = "c:/neo4j-data";
	
	private static final HashMap<Point, Integer> points =
		new HashMap<Point, Integer>();
	
	/**
	 * IMPORTANT: This application assumes the graph DB is already 
	 * populated with Pleiades places.
	 * 
	 * @param args no arguments needed
	 * @throws IOException if anything goes wrong while saving the image
	 */
	public static void main(String[] args) throws IOException {		
		PelagiosGraphBuilder graphBuilder = new PelagiosGraphBuilder(DATA_DIR);
		PelagiosGraph graph = graphBuilder.build();

		// TODO get all the places from the graph
		List<Place> places = new ArrayList<Place>();
		System.out.println(places.size() + " unique geolocated places");
		
		BufferedImage img = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setPaint(new Color(0, 0, 0));
		g.fillRect(0, 0, 640, 480);
		
		int max = 1;
		for (Place p : places) {
			Point xy = transform(p.getLon(), p.getLat());
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
			g.setPaint(new Color(rg, rg, brightness));
			g.fillOval(p.x - 2, p.y - 2, 3, 3);
		}
		
		ImageIO.write(img, "PNG", new File("test.png"));
	}

	private static Point transform(double lon, double lat) {
		double x = lon / 180 * 1200 + 160;
		double y = - lat / 90 * 768 + 540;
		return new Point((int) x, (int) y);
	}
}
