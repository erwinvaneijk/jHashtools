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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.minjus.nfi.dt.jhashtools.DigestResult;

/**
 * The AbstractFileHasher is needed to avoid duplication of code.
 *
 * @author Erwin van Eijk
 */
abstract class AbstractFileHasher implements FileHasher
{
    public static final int BLOCK_READ_SIZE = 1024 * 1024;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractFileHasher.class);

    private final Collection<DigestAlgorithm> digests;

    public AbstractFileHasher()
    {
        this.digests = new ArrayList<DigestAlgorithm>();
    }

    public AbstractFileHasher(final DigestAlgorithm digestAlgorithm) throws NoSuchAlgorithmException
    {
        this();
        this.addAlgorithm(digestAlgorithm);
    }

    public AbstractFileHasher(final Collection<DigestAlgorithm> algorithms) throws NoSuchAlgorithmException
    {
        this();
        for (final DigestAlgorithm algorithmName : algorithms) {
            this.addAlgorithm(algorithmName);
        }
    }

    /**
     * Add an algorithm to the set of supported algorithms.
     *
     * @param algorithmName
     *            the algorithm to add.
     */
    @Override
    public void addAlgorithm(final DigestAlgorithm algorithmName) {
        this.digests.add(algorithmName);
    }

    /**
     * Compute the digest(s) for the contents of the file.
     *
     * @param file
     *            the File to compute the digests for.
     *
     * @return a DigestResult containing the result of the computation.
     *
     * @throws FileNotFoundException
     *             thrown when <c>file</c> doesn't exist.
     * @throws IOException
     *             thrown when some IOException occurs.
     */
    @Override
    public abstract DigestResult getDigest(File file) throws FileNotFoundException, IOException;

    /**
     * Get all the supported digests.
     *
     * @return the digests
     */
    public Collection<DigestAlgorithm> getDigests() {
        return this.digests;
    }

    protected synchronized Collection<MessageDigest> getMessageDigests() {
        final Collection<MessageDigest> messageDigests = new ArrayList<MessageDigest>(this.digests.size());
        for (final DigestAlgorithm algorithm : this.digests) {
            try {
                messageDigests.add(algorithm.getInstance());
            } catch (final NoSuchAlgorithmException ex) {
                // this cannot happen, we've already checked them before.
                LOG.error("Digest algorithm {} is not available", algorithm, ex);
            }
        }
        return messageDigests;
    }
}