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
    public final void addAlgorithm(final String algorithm) throws NoSuchAlgorithmException {
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
