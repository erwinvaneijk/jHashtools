/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author kojak
 */
public class DirHasher {

    private DirHasherResult results;

    private Set<String> algorithms;
    private boolean verbose;

    public DirHasher() {
        results = new DirHasherResult();
        algorithms = new TreeSet<String>();
    }

    public DirHasher(String algorithm) throws NoSuchAlgorithmException {
        this();
        this.algorithms.add(MessageDigest.getInstance(algorithm).getAlgorithm());
    }

    public void addAlgorithm(String algorithm) {
        algorithms.add(algorithm);
    }

    public DirHasherResult getDigests(final File startFile) {
        if (! startFile.exists()) {
            throw new IllegalArgumentException(String.format("File %s does not exist", startFile.toString()));
        }
        updateDigests(this.results, startFile);
        return this.results;
    }

    public void updateDigests(DirHasherResult digests, File file) {
        if (! file.exists()) {
            throw new IllegalArgumentException(String.format("File %s does not exist", file.toString()));
        }

        try {
            FileWalker walker = new FileWalker();
            DirVisitor visitor = new DirVisitor(algorithms, digests);
            visitor.setVerbose(this.verbose);
            walker.addWalkerVisitor(visitor);
            walker.walk(file);
        } catch (NoSuchAlgorithmException ex) {
            // ignore. This should not happen, because we've already checked
            // the algorithms in the constructor;
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return this.verbose;
    }
}
