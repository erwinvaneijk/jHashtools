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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirVisitor implements WalkerVisitor {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private DirHasherResult resultMap;
    private boolean verbose;
    private final Set<String> algorithms;
        
    public DirVisitor() {
        this.resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>();
        this.algorithms.add(FileHasher.DEFAULT_ALGORITHM);
        this.verbose = false;
    }

    public DirVisitor(String algorithm) throws NoSuchAlgorithmException {
        resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>();
        this.algorithms.add(MessageDigest.getInstance(algorithm).getAlgorithm());
        this.verbose = false;
    }

    public DirVisitor(Collection<String> algorithms, DirHasherResult digests) throws NoSuchAlgorithmException {
        this(algorithms, false);
        this.resultMap = digests;
    }

    public DirVisitor(Collection<String> algorithms, boolean verbose) throws NoSuchAlgorithmException {
        resultMap = new DirHasherResult();
        this.algorithms = new TreeSet<String>();
        for (String algorithm: algorithms) {
            this.algorithms.add(MessageDigest.getInstance(algorithm).getAlgorithm());
        }
        this.verbose = verbose;
    }

    @Override
    public void visit(File theFile) {
        try {
            // FIXME
            // This should better be handled with an aspect, instead of this clutter.
            if (this.verbose) {
                this.logger.log(Level.INFO, "Processing file [" + theFile.toString() + "]");
            }
            
            DigestResult res = FileHasher.computeDigest(theFile, this.algorithms);
            resultMap.put(theFile, res);
        } catch (FileNotFoundException ex) {
            // ignore
        } catch (IOException ex) {
            this.logger.log(Level.SEVERE, "Got IOException while processing " + theFile.toString());
        } catch (NoSuchAlgorithmException e) {
            this.logger.log(Level.SEVERE, "Got an NoSuchAlgorithmException", e);
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
     * @param verbose set the verbosity of this visitor.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
