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
