package org.pelagios.backend;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	
	public static Config instance = null;
	
	private Properties props;
	
	private static final String NEO4J_DIR = "neo4j.dir";
	
	private Config() {
		props = new Properties();
		InputStream is =
			this.getClass().getClassLoader().getResourceAsStream("config.properties");
		
		if (is == null)
			throw new RuntimeException("Fatal error: config.properties not found");
		
		try {
			props.load(is);
		} catch (IOException e) {
			throw new RuntimeException("Fatal error: could not load config.properties: " + e.getMessage());
		}
	}
	
	public static Config getInstance() {
		if (instance == null)
			instance = new Config();
		return instance;
	}
	
	public String getNeo4jDirectory() {
		return props.getProperty(NEO4J_DIR);
	}

}
