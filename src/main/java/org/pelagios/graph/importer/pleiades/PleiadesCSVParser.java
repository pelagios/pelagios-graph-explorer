package org.pelagios.graph.importer.pleiades;

import org.apache.log4j.Logger;
import org.pelagios.graph.importer.pleiades.locations.LocationParser;
import org.pelagios.graph.importer.pleiades.names.NameParser;

/**
 * Common functionality useful for parsing Pleiades CSV dump files. {@link LocationParser} and
 * {@link NameParser} both extend this class.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public abstract class PleiadesCSVParser {

    protected static final String JSON_NULL = "null";

    protected Logger log = Logger.getLogger(PleiadesCSVParser.class);

    protected String logLine(String[] line) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < line.length; i++) {
            sb.append(line[i] + ",");
        }
        return sb.substring(0, sb.length() - 1);
    }

}
