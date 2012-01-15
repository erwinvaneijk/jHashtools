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

package nl.minjus.nfi.dt.jhashtools.hashers;

import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Visit all the files in a directory tree.
 */
public class DirectoryVisitor implements WalkerVisitor
{
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private DirHasherResult resultMap;
    private boolean verbose;
    private final FileHasher fileHasher;

    public DirectoryVisitor(DigestAlgorithm algorithm) throws NoSuchAlgorithmException
    {
        resultMap = new DirHasherResult();
        this.verbose = false;
        this.fileHasher = FileHasherCreator.create(null, algorithm);
    }

    public DirectoryVisitor() throws NoSuchAlgorithmException
    {
        resultMap = new DirHasherResult();
        this.verbose = false;
        this.fileHasher = FileHasherCreator.create(null);
    }

    public DirectoryVisitor(Collection<DigestAlgorithm> algorithms, DirHasherResult digests)
        throws NoSuchAlgorithmException
    {
        this(algorithms, false);
        this.resultMap = digests;
    }

    public DirectoryVisitor(Collection<DigestAlgorithm> algorithms, boolean verbose)
        throws NoSuchAlgorithmException
    {
        resultMap = new DirHasherResult();
        this.fileHasher = FileHasherCreator.createSimple(algorithms);
        this.verbose = verbose;
    }

    public void visit(File theFile) {
        try {
            final DigestResult res = this.fileHasher.getDigest(theFile);
            resultMap.put(theFile, res);
        } catch (FileNotFoundException ex) {
            this.logger.log(Level.SEVERE, "File not found: " + theFile.getPath());
        } catch (IOException ex) {
            this.logger.log(Level.SEVERE, "Got IOException while processing " + theFile.toString());
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
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
