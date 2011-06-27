package org.pelagios.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.pelagios.clustering.Cluster;
import org.pelagios.clustering.ClusterBuilder;
import org.pelagios.graph.PelagiosGraph;
import org.pelagios.graph.Place;
import org.pelagios.graph.builder.PelagiosGraphBuilder;

/**
 * A utility class that renders the places contained in
 * the Pelagios graph to an image, using basic distance-
 * based clustering to reduce the amount of placemarks.
 * 
 * TODO implement this
 * 
 * TODO turn this mess into a decent implementation
 * 
 * @author Rainer Simon
 */
public class RenderClusters {
			
	private static final String DATA_DIR = "c:/neo4j-data";
	
	private static final String IMAGE_FILE_NAME = "test.png";
	
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
		for (Place p : graph.listPlaces()) {
			places.add(p);
		}
		System.out.println(places.size() + " places");
		
		ClusterBuilder clusterBuilder = new ClusterBuilder(places);
		List<Cluster> clusters = clusterBuilder.build(6);
		System.out.println(clusters.size() + " clusters");
		
		BufferedImage img = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setPaint(new Color(0, 0, 0));
		g.fillRect(0, 0, 640, 480);
		g.setPaint(new Color(128, 128, 255));
				
		int max = 0;
		for (Cluster c : clusters) {
			if (c.size() > max)
				max = c.size();
		}
		System.out.println("Max cluster size: " + max);
		
		double delta = 10.0 / max;
		for (Cluster c : clusters) {
			Point p = transform(c.getLon(), c.getLat());
			int size = (int) (delta * c.size()) + 1;
			g.fillOval(p.x - size, p.y - size, size * 2, size * 2);
		}
		
		ImageIO.write(img, "PNG", new File(IMAGE_FILE_NAME));
	}

	private static Point transform(double lon, double lat) {
		// TODO well... do a decent projection here!
		double x = lon / 180 * 1200 + 160;
		double y = - lat / 90 * 768 + 540;
		return new Point((int) x, (int) y);
	}

}
