package org.pelagios.rendering;

import java.awt.Color;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory;

/**
 * Just a quick hack to construct basic line/point styles for shapefile
 * rendering.
 * 
 * @author Rainer Simon
 */
public class BasicMapStyles {
	
	/**
	 * GeoTools style factory
	 */
	private static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
	
	/**
	 * GeoTools filter factory
	 */
	private static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);
	
	public static Style line(Color color, double width, double opacity) {
        Stroke stroke = styleFactory.createStroke(
        		filterFactory.literal(color),
                filterFactory.literal(width),
                filterFactory.literal(opacity)
        );
        LineSymbolizer symbolizer = styleFactory.createLineSymbolizer(stroke, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(symbolizer);
        
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(styleFactory.createFeatureTypeStyle(
        		new Rule[]{rule}
        ));
        return style;		
	}
	
    public static Style point(Color stroke, Color fill, int strokeSize, int pointSize) {
    	Mark m = styleFactory.getCircleMark();
        m.setStroke(styleFactory.createStroke(
                filterFactory.literal(stroke), 
                filterFactory.literal(strokeSize))
        );
        m.setFill(styleFactory.createFill(
        		filterFactory.literal(fill))
        );

        Graphic g = styleFactory.createDefaultGraphic();
        g.graphicalSymbols().clear();
        g.graphicalSymbols().add(m);
        g.setSize(filterFactory.literal(pointSize));

        PointSymbolizer sym = styleFactory.createPointSymbolizer(g, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(styleFactory.createFeatureTypeStyle(
        		new Rule[]{rule}
        ));
        return style;
    }

}
