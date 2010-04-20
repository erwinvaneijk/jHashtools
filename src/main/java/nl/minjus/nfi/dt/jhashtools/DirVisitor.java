package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirVisitor implements WalkerVisitor {

    private final DirHasherResult resultMap;
    private Set<String> algorithms;
    private boolean verbose;

    public DirVisitor() {
        this.resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>();
        this.algorithms.add(FileHasher.DEFAULT_ALGORITHM);
        this.verbose = false;
    }

    public DirVisitor(Collection<String> algorithms) {
        resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>(algorithms);
        this.verbose = false;
    }

    public DirVisitor(String algorithm) {
        resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>();
        this.algorithms.add(algorithm);
        this.verbose = false;
    }

    DirVisitor(Collection<String> algorithms, DirHasherResult digests) {
        resultMap = digests;
        this.algorithms = new TreeSet<String>(algorithms);
        this.verbose = false;
    }

    DirVisitor(Set<String> algorithms, boolean verbose) {
        resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>(algorithms);
        this.verbose = verbose;
    }

    @Override
    public boolean visit(File theFile) {
        try {
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
        return true;
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
