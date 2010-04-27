/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.minjus.nfi.dt.jhashtools.persistence.JsonPersister;
import nl.minjus.nfi.dt.jhashtools.persistence.Persist;

/**
 *
 * @author kojak
 */
public class DirHasherResultVerifier {

    private DirHasherResult measuredDigests;
    private DirHasherResult verificationDigests;

    public DirHasherResultVerifier(DirHasherResult result) {
        this.measuredDigests = result;
    }

    public void loadDigestsFromFile(String filename) {
        DirHasherResult result = null;
        InputStream stream;
        try {
            File inputFile = new File(filename);
            stream = new FileInputStream(inputFile);
            Persist persist = new JsonPersister();
            result = (DirHasherResult) persist.load(stream, DirHasherResult.class);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.verificationDigests = result;
        }
    }

    public void verify(PrintStream out) {
        DirHasherResult differences = this.verificationDigests.notIntersect(this.measuredDigests);
        if (differences.size() == 0) {
            out.println("There are no differences.");
        } else {
            if (differences.containsKey("./hashes.txt")) {
                out.println("There are no differences.");
                out.println("hashes.txt:");
                for (Digest d : differences.get("./hashes.txt")) {
                    out.printf("\t%s\n", d.toString());
                }
            } else {
                out.println("There are differences.");
                for (Map.Entry<String, DigestResult> entry : differences.entrySet()) {
                    out.println(entry.getKey());
                    for (Digest d : entry.getValue()) {
                        out.printf("\t%s\n", d.toString());
                    }
                }
            }
        }
    }
}
