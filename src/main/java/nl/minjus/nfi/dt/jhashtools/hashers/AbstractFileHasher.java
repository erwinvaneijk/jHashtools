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

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * @author Erwin van Eijk
 */
abstract class AbstractFileHasher implements FileHasher
{
    public static final int BLOCK_READ_SIZE = 1024 * 1024;
    private static final Logger LOG = Logger.getLogger(AbstractFileHasher.class.getName());
    protected Collection<DigestAlgorithm> digests;

    public AbstractFileHasher()
    {
        this.digests = new ArrayList<DigestAlgorithm>();
    }

    public AbstractFileHasher(DigestAlgorithm digestAlgorithm) throws NoSuchAlgorithmException
    {
        this();
        this.addAlgorithm(digestAlgorithm);
    }

    public AbstractFileHasher(Collection<DigestAlgorithm> algorithms) throws NoSuchAlgorithmException
    {
        this();
        for (DigestAlgorithm algorithmName : algorithms) {
            this.addAlgorithm(algorithmName);
        }
    }

    /**
     * Add an algorithm to the set of supported algorithms.
     *
     * @param algorithmName the algorithm to add.
     *
     * @throws NoSuchAlgorithmException when the algorithm is not supported by the underlying JVM.
     */
    @Override
    public void addAlgorithm(DigestAlgorithm algorithmName) throws NoSuchAlgorithmException
    {
        this.digests.add(algorithmName);
    }

    /**
     * Compute the digest(s) for the contents of the file.
     *
     * @param file the File to compute the digests for.
     *
     * @return a DigestResult containing the result of the computation.
     *
     * @throws FileNotFoundException thrown when <c>file</c> doesn't exist.
     * @throws IOException           thrown when some IOException occurs.
     */
    @Override
    public abstract DigestResult getDigest(File file) throws FileNotFoundException, IOException;

    protected synchronized Collection<MessageDigest> getMessageDigests() {
        Collection<MessageDigest> messageDigests = new ArrayList<MessageDigest>(this.digests.size());
        for (DigestAlgorithm algorithm : this.digests) {
            try {
                messageDigests.add(algorithm.getInstance());
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();           // this cannot happen, we've already checked them before.
            }
        }
        return messageDigests;
    }
}