package org.pelagios.renderer;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.pelagios.rendering.ShapefileRenderer;

public class RenderWorldShapeFile {
	
	/**
	 * World map shapefile
	 */
	private static final String SHAPEFILE = "src\\test\\resources\\110m-coastline\\110m_coastline.shp";
	
	/**
	 * Result file
	 */
	private static final String OUTPUT_FILE = "rendered-map.png";

	public static void main(String[] args) throws IOException {
		// Create a 640x400 pixel drawing canvas
		Rectangle paintArea = new Rectangle(0, 0, 640, 400);
		BufferedImage bg = new BufferedImage(
				(int) paintArea.getWidth(),
				(int) paintArea.getHeight(), 
				BufferedImage.TYPE_INT_ARGB);
		
		// Render the world map shapefile
		ShapefileRenderer renderer = new ShapefileRenderer(new File(SHAPEFILE));
		renderer.renderShapefile(bg.createGraphics(), paintArea);
		
		ImageIO.write(bg, "png", new File(OUTPUT_FILE));  	
	}
	       
    /*
    List<MappedPosition> positions = new ArrayList<MappedPosition>();
    positions.add(new MappedPosition(new DirectPosition2D(-180, 90), new DirectPosition2D(0, 0)));        
    positions.add(new MappedPosition(new DirectPosition2D(180, 90), new DirectPosition2D(width, 0)));
    positions.add(new MappedPosition(new DirectPosition2D(180, -90), new DirectPosition2D(width, height)));
    AffineTransformBuilder atb = new AffineTransformBuilder(positions);
    
    JTS.transform(geom, transform)
   
            
    DirectPosition s = new DirectPosition2D(16, 48);
    DirectPosition d = new DirectPosition2D();
    d = mt.transform(s, d);
    System.out.println(s);
    
    graphics2d.setPaint(Color.MAGENTA);
    graphics2d.fillRect(100, 100, 5, 5);
    graphics2d.fillRect((int)d.getCoordinate()[0] - 3, (int)d.getCoordinate()[1] - 3, 5, 5);
    */

}
