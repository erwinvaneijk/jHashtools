package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirVisitor implements WalkerVisitor {

    private final Map<String, DigestsResults> resultMap;

    private String algorithm;

    public DirVisitor() {
        resultMap = new HashMap<String, DigestsResults>();
        this.algorithm = FileHasher.DEFAULT_ALGORITHM;
    }

    public DirVisitor(String algorithm) {
        resultMap = new HashMap<String, DigestsResults>();
        this.algorithm = algorithm;
    }

    @Override
    public boolean visit(File theFile) {
        try {
            byte[] digest = FileHasher.computeDigest(theFile, this.algorithm);
            DigestsResults res = new DigestsResults(FileHasher.DEFAULT_ALGORITHM, digest);
            resultMap.put(theFile.toString(), res);
        } catch (FileNotFoundException ex) {
            // ignore
        } catch (IOException ex) {
            Logger.getLogger(DirVisitor.class.getName()).log(Level.SEVERE, "Got IOException in %s", theFile.toString());
        }
        return true;
    }

    public final Map<String, DigestsResults> getResults() {
        return this.resultMap;
    }
}
