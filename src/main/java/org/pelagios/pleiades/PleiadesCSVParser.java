package org.pelagios.pleiades;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PleiadesCSVParser {
	
	protected static final String JSON_NULL = "null";
	
	protected Logger log = LoggerFactory.getLogger(PleiadesCSVParser.class);

	protected String logLine(String[] line) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<line.length; i++) {
			sb.append(line[i] + ",");
		}
		return sb.substring(0, sb.length() - 1);
	}

}
