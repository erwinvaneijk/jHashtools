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

import static java.util.logging.Logger.getLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger LOG = getLogger(DigestOutputCreator.class.getCanonicalName());
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
     * Gnerate the digests for the algorithms starting at <c>anArrayOfPathNames<c>.
     *
     * @param anArrayOfPathNames
     */
    public void generate(final String[] anArrayOfPathNames) {
        for (final String pathname : anArrayOfPathNames) {
        	try {
        		directoryHasher.updateDigests(digests, new File(pathname));
        	} catch (Throwable t) {
        		LOG.log(Level.FINEST, "We don't care", t);
        	}
        }
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
            LOG.log(Level.INFO, "Writing the results to " + outputFile.getName());
            file = new FileOutputStream(outputFile);
            final PersistenceProvider persistenceProvider = PersistenceProviderCreator
                .create(this.persistenceStyle);
            persistenceProvider.persist(file, digests);
            file.flush();

            final DirectoryHasher directoryHasher = DirectoryHasherCreator.create(null, digests.firstEntry()
                .getValue().getAlgorithms());
            return directoryHasher.getDigests(outputFile);
        } catch (final PersistenceException ex) {
            LOG.log(Level.SEVERE, "Cannot persist content to file", ex);
        } catch (final IOException ex) {
            LOG.log(Level.SEVERE, "Cannot create file", ex);
        } catch (final NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "Cannot create the algorithm", ex);
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (final IOException ex) {
                LOG.log(Level.SEVERE, "Cannot close file", ex);
            }
        }
        return null;
    }
}
