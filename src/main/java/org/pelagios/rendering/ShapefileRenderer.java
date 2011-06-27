package org.pelagios.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.renderer.lite.StreamingRenderer;

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
	
	public ReferencedEnvelope renderShapefile(Graphics2D g2d, Rectangle paintArea) {
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
        
        // Return the map extent so that users of this method can construct the
        // affine transformation afterwards for adding additional overlays
        return new ReferencedEnvelope(map.getAreaOfInterest());
	}

}
