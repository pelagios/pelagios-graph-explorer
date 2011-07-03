package org.pelagios.pleiades.names;

/**
 * The data contained in one line of the 'pleiades-names'
 * CSV dump.
 * 
 * @author Rainer Simon
 */
public class NameRecord {
	
	private String pid;
	
	private String nameTransliterated;

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPid() {
		return pid;
	}

	public void setNameTransliterated(String nameTransliterated) {
		this.nameTransliterated = nameTransliterated;
	}

	public String getNameTransliterated() {
		return nameTransliterated;
	}

}
