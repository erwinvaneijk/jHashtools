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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @Author Erwin van Eijk
 */
class FileHasher {

    public static final String DEFAULT_ALGORITHM = "sha-256";
    public static final String NO_ALGORITHM = "none";
    private static final int BLOCK_READ_SIZE = 1024*1024;

    private List<MessageDigest> digests;

    public static DigestResult computeDigest(File file, String algorithm)
            throws IOException, NoSuchAlgorithmException {
        FileHasher hasher = new FileHasher(algorithm);
        return hasher.getDigest(file);
    }

    public static DigestResult computeDigest(File file, Collection<String> algorithms)
            throws IOException, NoSuchAlgorithmException {
        FileHasher hasher = new FileHasher(algorithms);
        return hasher.getDigest(file);
    }

    public static DigestResult computeDigest(File file)
            throws IOException, NoSuchAlgorithmException {
        return FileHasher.computeDigest(file, "sha-256");
    }

    public FileHasher(String algorithm) throws NoSuchAlgorithmException {
        if (!algorithm.equals(NO_ALGORITHM)) {
            this.digests = new ArrayList<MessageDigest>();
            this.digests.add(MessageDigest.getInstance(algorithm));
        }
    }

    public FileHasher(Collection<String> algorithms) throws NoSuchAlgorithmException {
        this.digests = new ArrayList<MessageDigest>();
        for (String algorithm : algorithms) {
            addAlgorithm(algorithm);
        }
    }

    /**
     * Add an algorithm to the set of supported algorithms.
     *
     * @param algorithm the algorithm to add.
     * @throws NoSuchAlgorithmException when the algorithm is not supported by the underlying JVM.
     */
    public void addAlgorithm(String algorithm) throws NoSuchAlgorithmException {
        if (algorithm.equals(NO_ALGORITHM)) {
            return;
        }
        this.digests.add(MessageDigest.getInstance(algorithm));
    }

    /**
     * Compute the digest(s) for the contents of the file.
     * 
     * @param file the File to compute the digests for.
     * @return a DigestResult containing the result of the computation.
     * @throws FileNotFoundException thrown when <c>file</c> doesn't exist.
     * @throws IOException thrown when some IOException occurs.
     */
    public DigestResult getDigest(File file) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("File %s does not exist", file.toString()));
        }
        FileInputStream stream = new FileInputStream(file);
        return getDigest(stream);
    }

    /**
     * Compute the digest(s) for the contents of the file.
     *
     * @param stream the stream to read.
     * @return the resulting digests.
     * @throws IOException when things go wrong with the IO.
     */
    public DigestResult getDigest(FileInputStream stream) throws IOException {
        reset();
        try {
            FileChannel channel = stream.getChannel();
            long size = channel.size();
            long offset = 0L;
            long bytesLeft = size;
            byte[] buf = new byte[BLOCK_READ_SIZE];
            do {
                long bytesToRead = Math.min(bytesLeft, BLOCK_READ_SIZE);
                //MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, offset, bytesToRead);
                stream.read(buf, 0, (int) bytesToRead);
                for (MessageDigest digest : digests) {
                    // digest.update(buffer);
                    digest.update(buf, 0, (int) bytesToRead);
                }

                bytesLeft -= bytesToRead;
                offset += bytesToRead;
            } while(bytesLeft > 0);
        } catch (IOException ex) {
            // pass
        } finally {
            stream.close();
        }

        return finalizeDigestResult();
    }

    public void reset() {
        for (MessageDigest digest: digests) {
            digest.reset();
        }
    }

    private DigestResult finalizeDigestResult() {
        DigestResult res = new DigestResult();
        for (MessageDigest digest : digests) {
            res.add(new Digest(digest.getAlgorithm(), digest.digest()));
        }
        return res;
    }
}
