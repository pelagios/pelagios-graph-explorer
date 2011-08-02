package org.pelagios.graph.importer;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for representing a 'hierarchy' of data sets in the PELAGIOS graph.
 * Example for a three level hierarchy:
 * TODO data sets are currently uniquely identified by their screen name - this needs to change!
 * TODO as a consequence, dataset names now include the full hierarchy in their name to 
 * avoid name conflicts - again, this needs to change! Example:
 * 
 * "GAP" (Level 0) -> "GAP:The Histories" (Level 1) -> "GAP:The Histories:Page 371" (Level 2)
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class Hierarchy {

    private List<String> levels;

    public Hierarchy(List<String> levels) {
        this.levels = levels;
    }

    public List<String> getLevels() {
        List<String> qNames = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        for (String level : levels) {
            qNames.add(sb.toString() + level);
            sb.append(level + ":");
        }
        return qNames;
    }

    @Override
    public boolean equals(Object arg) {
        if (!(arg instanceof Hierarchy))
            return false;

        Hierarchy other = (Hierarchy) arg;
        if (this.levels.size() != other.levels.size())
            return false;

        for (int i = 0; i < levels.size(); i++) {
            if (!this.levels.get(i).equals(other.levels.get(i)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        String sl = "";
        for (String s : levels) {
            sl += s;
        }
        return sl.hashCode();
    }

}
