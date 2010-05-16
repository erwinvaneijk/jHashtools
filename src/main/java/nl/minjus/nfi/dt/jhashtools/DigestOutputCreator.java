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
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.SerialDirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProvider;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProviderCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceStyle;

import java.io.*;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

/**
 *
 * @author Erwin van Eijk
 */
public class DigestOutputCreator {

    private static Logger log = getLogger(DigestOutputCreator.class.getCanonicalName());
    private PrintWriter out;
    private DirectoryHasher directoryHasher;
    private File outputFile;
    private DirHasherResult digests;
    private PersistenceStyle persistenceStyle;
    private boolean forceOverwrite;

    public DigestOutputCreator(OutputStream out, DirectoryHasher directoryHasher, boolean forceOverwrite) {
        this.out = new PrintWriter(new OutputStreamWriter(out, Charset.forName("utf-8")));
        this.directoryHasher = directoryHasher;
        this.digests = new DirHasherResult();
        this.outputFile = null;
        this.forceOverwrite = forceOverwrite;
    }

    public void setPersistenceStyle(PersistenceStyle style) {
        this.persistenceStyle = style;
    }

    public void setOutputFile(String filename) throws FileNotFoundException {
        File file = new File(filename);
        if (!file.exists() || this.forceOverwrite) {
            this.outputFile = file;
        } else {
            throw new FileNotFoundException("File ["+filename+") not found");
        }
    }

    public void generate(String[] pathnames) {
        for (String pathname: pathnames) {
            directoryHasher.updateDigests(digests, new File(pathname));
        }
    }

    public void finish() {
        DirHasherResult result = this.persistDigestsToFile();
        this.out.printf("Generated with hashtree (java) by %s\n", System.getProperty("user.name"));
        result.prettyPrint(this.out);
    }

    private DirHasherResult persistDigestsToFile() {
        FileOutputStream file = null;
        try {
            log.log(Level.INFO, "Writing the results to " + outputFile.getName());
            file = new FileOutputStream(outputFile);
            PersistenceProvider persistenceProvider = PersistenceProviderCreator.create(this.persistenceStyle);
            persistenceProvider.persist(file, digests);
            file.flush();

            DirectoryHasher d = new SerialDirectoryHasher(digests.firstEntry().getValue().getAlgorithms());
            return d.getDigests(outputFile);
        } catch (PersistenceException ex) {
            log.log(Level.SEVERE, "Cannot persist content to file", ex);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Cannot create file", ex);
        } catch (NoSuchAlgorithmException ex) {
            log.log(Level.SEVERE, "Cannot create the algorithm", ex);
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Cannot close file", ex);
            }
        }
        return null;
    }
}
