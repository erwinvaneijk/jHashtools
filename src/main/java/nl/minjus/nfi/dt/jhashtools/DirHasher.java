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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Erwin van Eijk
 */
public class DirHasher {

    private final DirHasherResult results;
    private final Set<String> algorithms;
    private boolean verbose;

    public DirHasher() {
        results = new DirHasherResult();
        algorithms = new TreeSet<String>();
    }

    public DirHasher(String algorithm) throws NoSuchAlgorithmException {
        this();
        if (! algorithm.equals(FileHasher.NO_ALGORITHM)) {
            this.algorithms.add(MessageDigest.getInstance(algorithm).getAlgorithm());
        }
    }

    public DirHasher(Collection<String> algorithms) throws NoSuchAlgorithmException {
        this();
        for (String algorithm: algorithms) {
            this.addAlgorithm(algorithm);
        }
    }

    public void addAlgorithm(String algorithm) throws NoSuchAlgorithmException {
        algorithms.add(MessageDigest.getInstance(algorithm).getAlgorithm());
    }

    public DirHasherResult getDigests(final File startFile) {
        if (! startFile.exists()) {
            throw new IllegalArgumentException(String.format("File %s does not exist", startFile.toString()));
        }
        updateDigests(this.results, startFile);
        return this.results;
    }

    public void updateDigests(DirHasherResult digests, File file) {
        if (! file.exists()) {
            throw new IllegalArgumentException(String.format("File %s does not exist", file.toString()));
        }

        try {
            FileWalker walker = new FileWalker();
            DirVisitor visitor = new DirVisitor(algorithms, digests);
            visitor.setVerbose(this.verbose);
            walker.addWalkerVisitor(visitor);
            walker.walk(file);
        } catch (NoSuchAlgorithmException ex) {
            // ignore. This should not happen, because we've already checked
            // the algorithms in the constructor;
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public Collection<String> getAlgorithms() {
        return this.algorithms;
    }
}
