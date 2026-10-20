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
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import nl.minjus.nfi.dt.jhashtools.DigestResult;

/**
 * A Creator class for creating new FileHasher instances.
 *
 * @author Erwin van Eijk
 */
public class FileHasherCreator
{
    /**
     * Create a new FileHasher instance that is using the given ExecutorService.
     *
     * @param anExecutorService
     *            the ExecutorService to use.
     * @return a FileHasher instance.
     */
    public static FileHasher create(final ExecutorService anExecutorService) {
        FileHasher hasher;
        if (anExecutorService == null) {
            hasher = new SerialFileHasher();
        } else {
            hasher = new ConcurrentFileHasher(anExecutorService);
        }
        return hasher;
    }

    /**
     * Create a new FileHasher instance that is using the given ExecutorService.
     *
     * @param anExecutorService
     *            the ExecutorService to use.
     * @param digests
     *            the names of the digests that should be supported.
     * @return a FileHasher instance.
     * @throws NoSuchAlgorithmException
     *             when a name of an unknown digest is passed.
     */
    public static FileHasher create(final ExecutorService anExecutorService, final Collection<DigestAlgorithm> digests)
        throws NoSuchAlgorithmException
    {
        FileHasher hasher;
        if (anExecutorService == null) {
            hasher = new SerialFileHasher(digests);
        } else {
            hasher = new ConcurrentFileHasher(digests);
        }
        return hasher;
    }

    /**
     * Create a new FileHasher instance that is using the given ExecutorService.
     *
     * @param anExecutorService
     *            the ExecutorService to use.
     * @param digest
     *            the name of the digests that should be supported.
     * @return a FileHasher instance.
     * @throws NoSuchAlgorithmException
     *             when a name of an unknown digest is passed.
     */
    public static FileHasher create(final ExecutorService anExecutorService, final DigestAlgorithm digest)
        throws NoSuchAlgorithmException
    {
        final Collection<DigestAlgorithm> digests = new ArrayList<DigestAlgorithm>();
        digests.add(digest);
        return create(anExecutorService, digests);
    }

    /**
     * Compute the digest on a given file with a given algorithm.
     *
     * @param file
     *            the File to process.
     * @param algorithm
     *            the algorithm to use.
     * @return the result.
     * @throws IOException
     *             when some IO could not be performed.
     * @throws NoSuchAlgorithmException
     *             when the name of an unknown digest algorithm is passed.
     */
    public static DigestResult computeDigest(final File file, final DigestAlgorithm algorithm) throws IOException,
        NoSuchAlgorithmException
    {
        final FileHasher hasher = FileHasherCreator.create(null, algorithm);
        return hasher.getDigest(file);
    }

    /**
     * Compute the digest on a given file with a given algorithm.
     *
     * @param file
     *            the File to process.
     * @param algorithms
     *            the algorithm to use.
     * @return the result.
     * @throws IOException
     *             when some IO could not be performed.
     * @throws NoSuchAlgorithmException
     *             when the name of an unknown digest algorithm is passed.
     */
    public static DigestResult computeDigest(final File file, final Collection<DigestAlgorithm> algorithms)
        throws IOException, NoSuchAlgorithmException
    {
        final FileHasher hasher = FileHasherCreator.create(null, algorithms);
        return hasher.getDigest(file);
    }

    /**
     * Compute the digest on a given file with the default algorithm.
     *
     * @param file
     *            the File to process.
     * @return the result.
     * @throws IOException
     *             when some IO could not be performed.
     * @throws NoSuchAlgorithmException
     *             when the name of an unknown digest algorithm is passed.
     */
    public static DigestResult computeDigest(final File file) throws IOException, NoSuchAlgorithmException {
        return computeDigest(file, DigestAlgorithmFactory.create("sha-256"));
    }

    /**
     * Create a new FileHasher instance, which is 100% guaranteed single threaded.
     *
     * @param algorithms
     *            the algorithm to use.
     * @return the result.
     * @throws NoSuchAlgorithmException
     *             when the name of an unknown digest algorithm is passed.
     */
    public static FileHasher createSimple(final Collection<DigestAlgorithm> algorithms)
        throws NoSuchAlgorithmException
    {
        return new SerialFileHasher(algorithms);
    }
}
