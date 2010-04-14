/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kojak
 */
public class DirHasher {

    private Map<String, DigestsResults> results;

    public DirHasher(String algorithm) {
        results = new HashMap<String, DigestsResults>();
    }

    public Map<String, DigestsResults> getDigests(final File startFile) {

        if (! startFile.exists()) {
            throw new IllegalArgumentException(String.format("File %s does not exist", startFile.toString()));
        }
        FileWalker walker = new FileWalker();
        DirVisitor visitor = new DirVisitor();
        walker.addWalkerVisitor(visitor);
        walker.walk(startFile);
        return visitor.getResults();
    }
}
