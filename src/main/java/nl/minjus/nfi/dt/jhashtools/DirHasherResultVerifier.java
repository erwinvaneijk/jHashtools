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
import java.util.Calendar;
import java.util.Map;

/**
 *
 * @author Erwin van Eijk
 */
public class DirHasherResultVerifier {

    private DirHasherResult measuredDigests;
    private DirHasherResult verificationDigests;
    private File file;
    private final PersistenceStyle persistenceStyle;
    private boolean ignoreCase;
    private final DirectoryHasher directoryHasher;

    public DirHasherResultVerifier(DirectoryHasher hasher, PersistenceStyle persistenceStyle) {
        this.persistenceStyle = persistenceStyle;
        this.measuredDigests = new DirHasherResult();
        this.directoryHasher = hasher;
        this.ignoreCase = false;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public boolean isIgnoringCase() {
        return this.ignoreCase;
    }

    public void generateDigests(String[] filesToProcess) {
        for (String pathname : filesToProcess) {
            this.directoryHasher.updateDigests(measuredDigests, new File(pathname));
        }
    }

    public void loadDigestsFromFile(String filename) throws FileNotFoundException, PersistenceException {
        DirHasherResult result = null;
        Reader reader;
        this.file = new File(filename);
        reader = new FileReader(this.file);

        PersistenceProvider persistenceProvider = PersistenceProviderCreator.create(this.persistenceStyle);

        result = (DirHasherResult) persistenceProvider.load(reader, DirHasherResult.class);
        this.verificationDigests = result;
    }

    public void verify(PrintWriter out) {
        if (this.isIgnoringCase()) {
            DirHasherResult ignoredCaseMeasurements = new DirHasherResult(this.isIgnoringCase());
            ignoredCaseMeasurements.putAll(this.measuredDigests);
            DirHasherResult ignoredCaseVerifications = new DirHasherResult(this.isIgnoringCase());
            ignoredCaseVerifications.putAll(this.verificationDigests);

            this.verificationDigests = ignoredCaseVerifications;
            this.measuredDigests = ignoredCaseMeasurements;
        }

        DirHasherResult differences = this.measuredDigests.notIntersect(this.verificationDigests);
        out.printf("*** %s ***\n", Calendar.getInstance().getTime().toString());
        if (differences.size() == 0) {
            out.println("*** PASSED VERIFICATION ***");
        } else {
            if (differences.size() == 1 && FileOperations.isSameFile(differences.iterator().next().getKey(), this.file)) {
                out.println("*** PASSED VERIFICATION ***");
                out.println("Printing info on " + this.file.getName());
                differences.prettyPrint(out);
            } else {
                out.println("There are differences.");
                // First calculate the files measured, but missing in the verification.
                differences = this.measuredDigests.missing(this.verificationDigests);
                out.println("These entries are seen, but missing in the " + this.file.getName() + " list");
                for (Map.Entry<File, DigestResult> entry : differences) {
                    out.println(entry.getKey().toString());
                    for (Digest d : entry.getValue()) {
                        out.printf("\t%s\n", d.toString('\t'));
                    }
                }

                out.println("These entries are in the " + this.file.getName() + " list, but not in the directory and/or files.");
                for (Map.Entry<File, DigestResult> entry : differences) {
                    out.println(entry.getKey().toString());
                    for (Digest d : entry.getValue()) {
                        out.printf("\t%s\n", d.toString('\t'));
                    }
                }
            }
        }
    }
}
