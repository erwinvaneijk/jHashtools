/*
 * Copyright (c) 2010 Erwin van Eijk <erwin.vaneijk@gmail.com>. All rights reserved.
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
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of <copyright holder>.
 */

package nl.minjus.nfi.dt.jhashtools;

import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProvider;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProviderCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceStyle;
import nl.minjus.nfi.dt.jhashtools.utils.FileOperations;

import java.io.*;
import java.util.Calendar;
import java.util.Map;

/**
 * Perform the verfication pass on the measurements and the results that were computed in another pass,
 * which are stored in some file.
 *
 * @author Erwin van Eijk
 */
public class DirHasherResultVerifier
{

    private DirHasherResult measuredDigests;
    private DirHasherResult verificationDigests;
    private File file;
    private final PersistenceStyle persistenceStyle;
    private boolean ignoreCase;
    private final DirectoryHasher directoryHasher;

    /**
     * Constructor.
     *
     * @param aHasher the hasher to use.
     * @param thePersistenceStyle the persistence style to use.
     */
    public DirHasherResultVerifier(DirectoryHasher aHasher, PersistenceStyle thePersistenceStyle)
    {
        this.persistenceStyle = thePersistenceStyle;
        this.measuredDigests = new DirHasherResult();
        this.directoryHasher = aHasher;
        this.ignoreCase = false;
    }

    public void setIgnoreCase(boolean ignoreCase)
    {
        this.ignoreCase = ignoreCase;
    }

    public boolean isIgnoringCase()
    {
        return this.ignoreCase;
    }

    /**
     * Generate the digests for all the paths in thePathsToProcess.
     *
     * @param thePathsToProcess which paths should be done.
     */
    public void generateDigests(String[] thePathsToProcess)
    {
        for (String pathName : thePathsToProcess) {
            this.directoryHasher.updateDigests(measuredDigests, new File(pathName));
        }
    }

    /**
     * Load the precomputed digests from <c>aFilename<c>.
     *
     * @param aFilename the file to read.
     * @throws FileNotFoundException when the file is not found.
     * @throws PersistenceException when the file could not be properly read.
     */
    public void loadDigestsFromFile(String aFilename) throws FileNotFoundException, PersistenceException
    {
        Reader reader;
        this.file = new File(aFilename);
        reader = new FileReader(this.file);

        final PersistenceProvider persistenceProvider = PersistenceProviderCreator.create(this.persistenceStyle);

        this.verificationDigests = persistenceProvider.load(reader, DirHasherResult.class);
    }

    /**
     * Actually perform the verification phase. Write the output to <c>anOutput<c>.
     *
     * @param anOutput where to write the output to.
     */
    public void verify(PrintWriter anOutput)
    {
        if (this.isIgnoringCase()) {
            final DirHasherResult ignoredCaseMeasurements = new DirHasherResult(this.isIgnoringCase());
            ignoredCaseMeasurements.putAll(this.measuredDigests);
            final DirHasherResult ignoredCaseVerifications = new DirHasherResult(this.isIgnoringCase());
            ignoredCaseVerifications.putAll(this.verificationDigests);

            this.verificationDigests = ignoredCaseVerifications;
            this.measuredDigests = ignoredCaseMeasurements;
        }

        DirHasherResult differences = this.measuredDigests.notIntersect(this.verificationDigests);
        anOutput.printf("*** %s ***\n", Calendar.getInstance().getTime().toString());
        if (differences.size() == 0) {
            anOutput.println("*** PASSED VERIFICATION ***");
        } else {
            if (differences.size() == 1
                    && FileOperations.isSameFile(differences.iterator().next().getKey(), this.file))
            {
                anOutput.println("*** PASSED VERIFICATION ***");
                anOutput.println("Printing info on " + this.file.getName());
                differences.prettyPrint(anOutput);
            } else {
                anOutput.println("There are differences.");
                // First calculate the files measured, but missing in the verification.
                differences = this.measuredDigests.missing(this.verificationDigests);
                anOutput.println("These entries are seen, but missing in the " + this.file.getName() + " list");
                for (Map.Entry<File, DigestResult> entry : differences) {
                    anOutput.println(entry.getKey().toString());
                    for (Digest d : entry.getValue()) {
                        anOutput.printf("\t%s\n", d.toString('\t'));
                    }
                }

                differences = this.verificationDigests.missing(this.measuredDigests);
                anOutput.println("These entries are in the "
                        + this.file.getName()
                        + " list, but not in the directory and/or files.");
                for (Map.Entry<File, DigestResult> entry : differences) {
                    anOutput.println(entry.getKey().toString());
                    for (Digest d : entry.getValue()) {
                        anOutput.printf("\t%s\n", d.toString('\t'));
                    }
                }
            }
        }
    }
}
