/*
 * Copyright (c) 2010. Erwin van Eijk <erwin.vaneijk@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.minjus.nfi.dt.jhashtools;

import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProvider;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProviderCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceStyle;
import nl.minjus.nfi.dt.jhashtools.utils.FileOperations;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Erwin van Eijk
 */
public class DirHasherResultVerifier {

    private final DirHasherResult measuredDigests;
    private DirHasherResult verificationDigests;
    private File file;
    private PersistenceStyle persistenceStyle;

    public DirHasherResultVerifier(DirHasherResult result, PersistenceStyle persistenceStyle) {
        this.persistenceStyle = persistenceStyle;
        this.measuredDigests = result;
    }

    public void loadDigestsFromFile(String filename) {
        DirHasherResult result = null;
        InputStream stream;
        try {
            this.file = new File(filename);
            stream = new FileInputStream(this.file);

            PersistenceProvider persistenceProvider = PersistenceProviderCreator.create(this.persistenceStyle);

            result = (DirHasherResult) persistenceProvider.load(stream, DirHasherResult.class);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (PersistenceException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Cannot load from file. Unfortunately", ex);
        } finally {
            this.verificationDigests = result;
        }
    }

    public void verify(PrintStream out) {
        DirHasherResult differences = this.measuredDigests.notIntersect(this.verificationDigests);
        if (differences.size() == 0) {
            out.println("There are no differences at all.");
        } else {
            if (differences.size() == 1 && FileOperations.isSameFile(differences.firstKey(), this.file)
                    ) {
                out.println("There are no differences.");
                out.println("Printing info on the output file.");
                differences.prettyPrint(out);
            } else {
                out.println("There are differences.");
                for (Map.Entry<File, DigestResult> entry : differences.entrySet()) {
                    out.println(entry.getKey().toString());
                    for (Digest d : entry.getValue()) {
                        out.printf("\t%s\n", d.toString('\t'));
                    }
                }
            }
        }
    }

    
}
