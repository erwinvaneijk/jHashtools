package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirVisitor implements WalkerVisitor {

    private DirHasherResult resultMap;
    private Set<String> algorithms;
    private boolean verbose;

    public DirVisitor() {
        this.resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>();
        this.algorithms.add(FileHasher.DEFAULT_ALGORITHM);
        this.verbose = false;
    }

    public DirVisitor(Collection<String> algorithms, boolean verbose) throws NoSuchAlgorithmException {
        resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>();
        for (String algorithm: algorithms) {
            this.algorithms.add(MessageDigest.getInstance(algorithm).getAlgorithm());
        }
        this.verbose = verbose;
    }

    public DirVisitor(String algorithm) throws NoSuchAlgorithmException {
        resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>();
        this.algorithms.add(MessageDigest.getInstance(algorithm).getAlgorithm());
        this.verbose = false;
    }

    DirVisitor(Collection<String> algorithms, DirHasherResult digests) throws NoSuchAlgorithmException {
        this(algorithms, false);
        this.resultMap = digests;
    }

    @Override
    public void visit(File theFile) {
        try {
            // FIXME
            // This should better be handled with an aspect, instead of this clutter.
            if (this.verbose) {
                Logger.getLogger(DirVisitor.class.getName()).log(Level.INFO, "Processing file [" + theFile.toString() + "]");
            }
            
            DigestResult res = FileHasher.computeDigest(theFile, this.algorithms);
            resultMap.put(theFile.toString(), res);
        } catch (FileNotFoundException ex) {
            // ignore
        } catch (IOException ex) {
            Logger.getLogger(DirVisitor.class.getName()).log(Level.SEVERE, "Got IOException while processing " + theFile.toString());
        }
    }

    public final DirHasherResult getResults() {
        return this.resultMap;
    }

    /**
     * @return get the verbosity
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * @param verbose set the verbosity of this visitor.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
