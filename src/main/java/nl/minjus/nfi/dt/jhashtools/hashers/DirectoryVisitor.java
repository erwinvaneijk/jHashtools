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

package nl.minjus.nfi.dt.jhashtools.hashers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.FileTime;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.minjus.nfi.dt.jhashtools.AccessTimes;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 * Visit all the files in a directory tree.
 */
public class DirectoryVisitor implements WalkerVisitor
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private DirHasherResult resultMap;
    private boolean verbose;
    private final FileHasher fileHasher;

    public DirectoryVisitor(final DigestAlgorithm algorithm) throws NoSuchAlgorithmException
    {
        resultMap = new DirHasherResult();
        this.verbose = false;
        this.fileHasher = FileHasherCreator.create(null, algorithm);
    }

    public DirectoryVisitor()
    {
        resultMap = new DirHasherResult();
        this.verbose = false;
        this.fileHasher = FileHasherCreator.create(null);
    }

    public DirectoryVisitor(final Collection<DigestAlgorithm> algorithms, final DirHasherResult digests)
        throws NoSuchAlgorithmException
    {
        this(algorithms, false);
        this.resultMap = digests;
    }

    public DirectoryVisitor(final Collection<DigestAlgorithm> algorithms, final boolean verbose)
        throws NoSuchAlgorithmException
    {
        resultMap = new DirHasherResult();
        this.fileHasher = FileHasherCreator.createSimple(algorithms);
        this.verbose = verbose;
    }

    @Override
    public void visit(final File theFile) {
        try {
            final FileTime accessTimeBefore = AccessTimes.lastAccessTime(theFile);
            final DigestResult res = this.fileHasher.getDigest(theFile);
            resultMap.put(theFile, res);
            resultMap.putAccessTimes(theFile, new AccessTimes(accessTimeBefore, AccessTimes.lastAccessTime(theFile)));
        } catch (final FileNotFoundException | NoSuchFileException ex) {
            this.logger.error("File not found: " + theFile.getPath());
        } catch (final IOException ex) {
            this.logger.error("Got IOException while processing " + theFile.toString());
        }
    }

    public final DirHasherResult getResults() {
        return this.resultMap;
    }

    /**
     * @return get the verbosity
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * @param verbose
     *            set the verbosity of this visitor.
     */
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
}
