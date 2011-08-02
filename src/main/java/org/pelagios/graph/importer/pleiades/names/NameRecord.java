package org.pelagios.graph.importer.pleiades.names;

/**
 * A simple class that wraps the (relevant) data contained in one
 * line of the 'pleiades-names' CSV dump.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
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
