package org.pelagios.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.referencing.operation.builder.AffineTransformBuilder;
import org.geotools.referencing.operation.builder.MappedPosition;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Envelope;

/**
 * A utility class for rendering shapefiles to a Java 2d graphics context.
 * 
 * @author Rainer Simon
 */
public class ShapefileRenderer {
	
	/**
	 * The shapefile
	 */
	private SimpleFeatureCollection shapefile;
	
	public ShapefileRenderer(File shapefile) throws IOException {
        FileDataStore store = FileDataStoreFinder.getDataStore(shapefile);
        this.shapefile = store.getFeatureSource().getFeatures();
	}
	
	/**
	 * Renders the shapefile to a Java 2D graphics context. The method returns
	 * the affine transform used by the renderer, so that users of this method 
	 * can add additional graphics afterwards.
	 *
	 * @param g2d the 2D graphics context
	 * @param paintArea the paint area on the context
	 * @return the affine transformation used for rendering
	 */
	public MathTransform renderShapefile(Graphics2D g2d, Rectangle paintArea) {
		// TODO make styling more flexible
        MapContext map = new DefaultMapContext();        
        map.addLayer(shapefile, BasicMapStyles.line(Color.BLACK, 0.5, 1));

        StreamingRenderer renderer = new StreamingRenderer();
        Map<Key, Object> hintsMap = new HashMap<Key, Object>();
        hintsMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RenderingHints hints = new RenderingHints(hintsMap);
        renderer.setJava2DHints(hints);
        
        renderer.setContext(map);
        renderer.paint(g2d, paintArea, map.getAreaOfInterest());  
        map.dispose();
        
        return computeAffineTransformation(map.getAreaOfInterest(), paintArea); 
	}
	
	/**
	 * Computes an affine transformation based on the four corner points of the
	 * map and paint areas.
	 * @param mapArea the map area
	 * @param paintArea the paint area
	 * @return the MathTransform
	 */
	private MathTransform computeAffineTransformation(Envelope mapArea, Rectangle paintArea) {
	    List<MappedPosition> positions = new ArrayList<MappedPosition>();
	    
	    // Top left
	    positions.add(new MappedPosition(
	    		new DirectPosition2D(mapArea.getMinX(), mapArea.getMaxY()),
	    		new DirectPosition2D(paintArea.getMinX(), paintArea.getMinY())));

	    // Top right
	    positions.add(new MappedPosition(
	    		new DirectPosition2D(mapArea.getMaxX(), mapArea.getMaxY()),
	    		new DirectPosition2D(paintArea.getMaxX(), paintArea.getMinY())));
	    
	    // Bottom left
	    positions.add(new MappedPosition(
	    		new DirectPosition2D(mapArea.getMinX(), mapArea.getMinY()),
	    		new DirectPosition2D(paintArea.getMinX(), paintArea.getMaxY())));
	    
	    // Bottom right
	    positions.add(new MappedPosition(
	    		new DirectPosition2D(mapArea.getMaxX(), mapArea.getMinY()),
	    		new DirectPosition2D(paintArea.getMaxX(), paintArea.getMaxY())));

	    try {
			return new AffineTransformBuilder(positions).getMathTransform();
		} catch (Exception e) {
			// Under normal circumstances, all should be well!
			throw new RuntimeException(e);
		}
	}

}
