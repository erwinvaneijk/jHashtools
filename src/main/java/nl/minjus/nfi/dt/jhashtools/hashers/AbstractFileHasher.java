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
 *
 * @Author Erwin van Eijk
 */
abstract class AbstractFileHasher implements FileHasher {

    public  static final int BLOCK_READ_SIZE = 1024 * 1024;
    private static final Logger log = Logger.getLogger(AbstractFileHasher.class.getName());
    protected Collection<MessageDigest> digests;

    public AbstractFileHasher() {
        this.digests = new ArrayList<MessageDigest>();
    }

    public AbstractFileHasher(MessageDigest digestAlgorithm) {
        this();
        this.digests.add(digestAlgorithm);
    }

    public AbstractFileHasher(Collection<MessageDigest> algorithms) {
        this();
        for (MessageDigest algorithm : algorithms) {
            addAlgorithm(algorithm);
        }
    }

    /**
     * Add an algorithm to the set of supported algorithms.
     *
     * @param algorithm the algorithm to add.
     * @throws NoSuchAlgorithmException when the algorithm is not supported by the underlying JVM.
     */
    @Override
    public void addAlgorithm(MessageDigest algorithm) {
        this.digests.add(algorithm);
    }

    /**
     * Compute the digest(s) for the contents of the file.
     *
     * @param file the File to compute the digests for.
     * @return a DigestResult containing the result of the computation.
     * @throws FileNotFoundException thrown when <c>file</c> doesn't exist.
     * @throws IOException thrown when some IOException occurs.
     */
    @Override
    public abstract DigestResult getDigest(File file) throws FileNotFoundException, IOException;

    @Override
    public void reset() {
        for (MessageDigest digest : digests) {
            digest.reset();
        }
    }

    protected DigestResult finalizeDigestResult() {
        DigestResult res = new DigestResult();
        for (MessageDigest digest : digests) {
            res.add(new Digest(digest.getAlgorithm(), digest.digest()));
        }
        return res;
    }
}
