package org.pelagios.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.geotools.geometry.DirectPosition2D;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.pelagios.backend.graph.PelagiosGraph;
import org.pelagios.backend.graph.Place;
import org.pelagios.backend.graph.builder.PelagiosGraphBuilder;
import org.pelagios.rendering.ShapefileRenderer;
import org.pelagios.rendering.clustering.Cluster;
import org.pelagios.rendering.clustering.ClusterBuilder;

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
	
	/**
	 * IMPORTANT: This application assumes the graph DB is already 
	 * populated with Pleiades places.
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

		// TODO get all the places from the graph
		List<Place> places = new ArrayList<Place>();
		for (Place p : graph.listPlaces()) {
			places.add(p);
		}
		System.out.println(places.size() + " places");
		
		// TODO need to figure out a way to define the
		// cluster threshold based on the actual pixel distance
		// on the screen!
		ClusterBuilder clusterBuilder = new ClusterBuilder(places);
		List<Cluster> clusters = clusterBuilder.build(6);
		System.out.println(clusters.size() + " clusters");
		
		int max = 0;
		for (Cluster c : clusters) {
			if (c.size() > max)
				max = c.size();
		}
		System.out.println("Max cluster size: " + max);
		
		g2d.setPaint(Color.RED);
		double delta = 10.0 / max;
		for (Cluster c : clusters) {
			Point p = transform(c.getLon(), c.getLat(), transform);
			int size = (int) (delta * c.size()) + 1;
			g2d.fillOval(p.x - size, p.y - size, size * 2, size * 2);
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
