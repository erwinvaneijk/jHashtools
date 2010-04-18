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

    public DirVisitor() {
        this.resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>();
        this.algorithms.add(FileHasher.DEFAULT_ALGORITHM);
    }

    public DirVisitor(Collection<String> algorithms) {
        resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>(algorithms);
    }

    public DirVisitor(String algorithm) {
        resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>();
        this.algorithms.add(algorithm);
    }

    DirVisitor(Collection<String> algorithms, DirHasherResult digests) {
        resultMap = digests;
        this.algorithms = new TreeSet<String>(algorithms);
    }

    @Override
    public boolean visit(File theFile) {
        try {
            DigestResult res = FileHasher.computeDigest(theFile, this.algorithms);
            resultMap.put(theFile.toString(), res);
        } catch (FileNotFoundException ex) {
            // ignore
        } catch (IOException ex) {
            Logger.getLogger(DirVisitor.class.getName()).log(Level.SEVERE, "Got IOException in %s", theFile.toString());
        }
        return true;
    }

    public final DirHasherResult getResults() {
        return this.resultMap;
    }
}
