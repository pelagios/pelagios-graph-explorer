package org.pelagios.pleiades.importer;

import org.apache.log4j.Logger;

public abstract class PleiadesCSVParser {
	
	protected static final String JSON_NULL = "null";
	
	protected Logger log = Logger.getLogger(PleiadesCSVParser.class);

	protected String logLine(String[] line) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<line.length; i++) {
			sb.append(line[i] + ",");
		}
		return sb.substring(0, sb.length() - 1);
	}

}
