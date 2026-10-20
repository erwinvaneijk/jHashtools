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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasherCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProvider;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProviderCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceStyle;

/**
 * Construct the output that can later on be used for verification.
 *
 * @author Erwin van Eijk
 */
public class DigestOutputCreator
{
    private static final Logger LOG = LoggerFactory.getLogger(DigestOutputCreator.class);
    private final PrintWriter out;
    private final DirectoryHasher directoryHasher;
    private File outputFile;
    private final DirHasherResult digests;
    private PersistenceStyle persistenceStyle;
    private final boolean forceOverwrite;

    /**
     * Constructor.
     *
     * @param anOutputStream
     *            the outputstream to write the final result to.
     * @param aDirectoryHasher
     *            the directoryHasher to get the results from.
     * @param theOverwriteOption
     *            whether or not to force overwriting.
     */
    public DigestOutputCreator(final OutputStream anOutputStream, final DirectoryHasher aDirectoryHasher,
        final boolean theOverwriteOption)
    {
        this.out = new PrintWriter(new OutputStreamWriter(anOutputStream, Charset.forName("utf-8")));
        this.directoryHasher = aDirectoryHasher;
        this.digests = new DirHasherResult();
        this.outputFile = null;
        this.forceOverwrite = theOverwriteOption;
        this.persistenceStyle = PersistenceStyle.JSON;
    }

    public void setPersistenceStyle(final PersistenceStyle thePersistenceStyle) {
        this.persistenceStyle = thePersistenceStyle;
    }

    public void setOutputFile(final String aFilename) throws FileNotFoundException {
        final File file = new File(aFilename);
        if (!file.exists() || this.forceOverwrite) {
            this.outputFile = file;
        } else {
            throw new FileNotFoundException("File [" + aFilename + ") not found");
        }
    }

    /**
     * Generate the digests for the algorithms starting at <c>anArrayOfPathNames<c>.
     *
     * @param anArrayOfPathNames
     */
    public void generate(final String[] anArrayOfPathNames) {
        for (final String pathname : anArrayOfPathNames) {
            try {
                directoryHasher.updateDigests(digests, new File(pathname));
            } catch (final Throwable t) {
                LOG.debug("We don't care", t);
            }
        }
        digests.getChangedAccessTimes().forEach((file, accessTimes) -> LOG.warn(
            "Access time of {} changed while hashing: {} -> {}", file, accessTimes.before(), accessTimes.after()));
    }

    /**
     * Finish the computation, and write the the finalizing information to <c>DigestOutputCreator#out<c>.
     */
    public void finish() {
        final DirHasherResult result = this.persistDigestsToFile();
        this.out.printf("Generated with hashtree (java) by %s\n", System.getProperty("user.name"));
        result.prettyPrint(this.out);
    }

    private DirHasherResult persistDigestsToFile() {
        FileOutputStream file = null;
        try {
            LOG.info("Writing the results to " + outputFile.getName());
            file = new FileOutputStream(outputFile);
            final PersistenceProvider persistenceProvider = PersistenceProviderCreator
                .create(this.persistenceStyle);
            persistenceProvider.persist(file, digests);
            file.flush();

            final DirectoryHasher directoryHasher = DirectoryHasherCreator.create(null, digests.firstEntry()
                .getValue().getAlgorithms());
            return directoryHasher.getDigests(outputFile);
        } catch (final PersistenceException ex) {
            LOG.error("Cannot persist content to file", ex);
        } catch (final IOException ex) {
            LOG.error("Cannot create file", ex);
        } catch (final NoSuchAlgorithmException ex) {
            LOG.error("Cannot create the algorithm", ex);
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (final IOException ex) {
                LOG.error("Cannot close file", ex);
            }
        }
        return null;
    }
}
