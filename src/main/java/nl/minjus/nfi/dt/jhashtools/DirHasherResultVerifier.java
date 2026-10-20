/*
 * Copyright (c) 2010 Erwin van Eijk <erwin.vaneijk@gmail.com>.
 *
 * Licensed under the EUPL, Version 1.2 only (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific language
 * governing permissions and limitations under the Licence.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProvider;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProviderCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceStyle;
import nl.minjus.nfi.dt.jhashtools.utils.FileOperations;

/**
 * Perform the verification pass on the measurements and the results that
 * were computed in another pass, which are stored in some file.
 *
 * @author Erwin van Eijk
 */
public class DirHasherResultVerifier
{
    private static final Logger LOG = LoggerFactory.getLogger(DirHasherResultVerifier.class);

    private DirHasherResult measuredDigests;
    private DirHasherResult verificationDigests;
    private File file;
    private final PersistenceStyle persistenceStyle;
    private boolean ignoreCase;
    private final DirectoryHasher directoryHasher;

    /**
     * Constructor.
     *
     * @param aHasher
     *            the hasher to use.
     * @param thePersistenceStyle
     *            the persistence style to use.
     */
    public DirHasherResultVerifier(final DirectoryHasher aHasher, final PersistenceStyle thePersistenceStyle)
    {
        this.persistenceStyle = thePersistenceStyle;
        this.measuredDigests = new DirHasherResult();
        this.directoryHasher = aHasher;
        this.ignoreCase = false;
    }

    public void setIgnoreCase(final boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public boolean isIgnoringCase() {
        return this.ignoreCase;
    }

    /**
     * Generate the digests for all the paths in thePathsToProcess.
     *
     * @param thePathsToProcess
     *            which paths should be done.
     */
    public void generateDigests(final String[] thePathsToProcess) {
        for (final String pathName : thePathsToProcess) {
            this.directoryHasher.updateDigests(measuredDigests, new File(pathName));
        }
        measuredDigests.getChangedAccessTimes().forEach((file, accessTimes) -> LOG.warn(
            "Access time of {} changed while hashing: {} -> {}", file, accessTimes.before(), accessTimes.after()));
    }

    /**
     * Load the precomputed digests from <code>aFilename</code>.
     *
     * @param aFilename
     *            the file to read.
     * @throws FileNotFoundException
     *             when the file is not found.
     * @throws PersistenceException
     *             when the file could not be properly read.
     */
    public void loadDigestsFromFile(final String aFilename) throws FileNotFoundException,
        PersistenceException
    {
        this.file = new File(aFilename);
        final PersistenceProvider persistenceProvider = PersistenceProviderCreator
            .create(this.persistenceStyle);

        try (Reader reader = new InputStreamReader(new FileInputStream(this.file), StandardCharsets.UTF_8)) {
            this.verificationDigests = persistenceProvider.load(reader, DirHasherResult.class);
        } catch (final FileNotFoundException ex) {
            throw ex;
        } catch (final IOException ex) {
            throw new PersistenceException("Could not close " + this.file, ex);
        }
    }

    /**
     * Actually perform the verification phase. Write the output to <c>anOutput<c>.
     *
     * @param anOutput
     *            where to write the output to.
     * @return <code>true</code> if the verification passed, <code>false</code> if there are differences.
     */
    public boolean verify(final PrintWriter anOutput) {
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
            return true;
        } else {
            if (differences.size() == 1
                && FileOperations.isSameFile(differences.iterator().next().getKey(), this.file))
            {
                anOutput.println("*** PASSED VERIFICATION ***");
                anOutput.println("Printing info on " + this.file.getName());
                differences.prettyPrint(anOutput);
                return true;
            } else {
                anOutput.println("There are differences.");
                // First calculate the files measured, but missing in the
                // verification.
                differences = this.measuredDigests.missing(this.verificationDigests);
                anOutput.println("These entries are seen, but missing in the " + this.file.getName()
                    + " list");
                for (final Map.Entry<File, DigestResult> entry : differences) {
                    anOutput.println(entry.getKey().toString());
                    for (final Digest d : entry.getValue()) {
                        anOutput.printf("\t%s\n", d.toString('\t'));
                    }
                }

                differences = this.verificationDigests.missing(this.measuredDigests);
                anOutput.println("These entries are in the " + this.file.getName()
                    + " list, but not in the directory and/or files.");
                for (final Map.Entry<File, DigestResult> entry : differences) {
                    anOutput.println(entry.getKey().toString());
                    for (final Digest d : entry.getValue()) {
                        anOutput.printf("\t%s\n", d.toString('\t'));
                    }
                }
                return false;
            }
        }
    }
}
