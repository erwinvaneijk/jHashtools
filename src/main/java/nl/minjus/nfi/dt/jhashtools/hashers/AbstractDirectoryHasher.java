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

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 * All directory hashers need the same functionality. Use AbstractDirectoryHasher to make life easier.
 *
 * @author Erwin van Eijk
 *
 */
public abstract class AbstractDirectoryHasher implements DirectoryHasher
{
    private final Collection<DigestAlgorithm> algorithms = new LinkedList<DigestAlgorithm>();
    private boolean verbose;

    public AbstractDirectoryHasher()
    {
    }

    public AbstractDirectoryHasher(final String algorithm) throws NoSuchAlgorithmException
    {
        this.addAlgorithm(algorithm);
    }

    public AbstractDirectoryHasher(final Collection<String> algorithms) throws NoSuchAlgorithmException
    {
        for (final String algorithm : algorithms) {
            this.addAlgorithm(algorithm);
        }
    }

    @Override
    public void setAlgorithms(final Collection<String> algorithms) throws NoSuchAlgorithmException {
        this.algorithms.clear();
        for (final String algorithm : algorithms) {
            addAlgorithm(algorithm);
        }
    }

    @Override
    public void addAlgorithm(final String algorithm) throws NoSuchAlgorithmException {
        algorithms.add(DigestAlgorithmFactory.create(algorithm));
    }

    @Override
    public abstract DirHasherResult getDigests(File startFile);

    @Override
    public abstract void updateDigests(DirHasherResult digests, File file);

    @Override
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public boolean isVerbose() {
        return this.verbose;
    }

    /**
     * Get the names of the algorithms.
     *
     * @return the names
     */
    @Override
    public final Collection<String> getAlgorithms() {
        final Collection<String> newSet = new LinkedList<String>();
        for (final DigestAlgorithm alg : this.algorithms) {
            newSet.add(alg.getName());
        }
        return newSet;
    }

    /**
     * @return the algorithms
     */
    public Collection<DigestAlgorithm> getTheAlgorithms() {
        return algorithms;
    }
}
