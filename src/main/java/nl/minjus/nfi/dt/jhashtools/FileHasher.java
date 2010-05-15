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
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Exchanger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @Author Erwin van Eijk
 */
class FileHasher {

    public static final String DEFAULT_ALGORITHM = "sha-256";
    public static final String NO_ALGORITHM = "none";
    private static final int BLOCK_READ_SIZE = 1024 * 1024;
    private static final Logger log = Logger.getLogger(FileHasher.class.getName());
    private Collection<MessageDigest> digests;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private Exchanger<ByteBuffer> exchanger;

    public static DigestResult computeDigest(File file, String algorithm)
            throws IOException, NoSuchAlgorithmException {
        FileHasher hasher = new FileHasher(MessageDigest.getInstance(algorithm));
        return hasher.getDigest(file);
    }

    public static DigestResult computeDigest(File file, Collection<MessageDigest> algorithms)
            throws IOException, NoSuchAlgorithmException {
        FileHasher hasher = new FileHasher(algorithms);
        return hasher.getDigest(file);
    }

    public static DigestResult computeDigest(File file)
            throws IOException, NoSuchAlgorithmException {
        return FileHasher.computeDigest(file, "sha-256");
    }

    public FileHasher() {
        this.digests = new ArrayList<MessageDigest>();
        this.exchanger = new Exchanger<ByteBuffer>();
    }

    public FileHasher(MessageDigest digestAlgorithm) {
        this();
        this.digests.add(digestAlgorithm);
    }

    public FileHasher(Collection<MessageDigest> algorithms) {
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
        return this.getDigestSingle(stream);
    }


    /**
     * Compute the digest(s) for the contents of the file.
     *
     * @param stream the stream to read.
     * @return the resulting digests.
     * @throws IOException when things go wrong with the IO.
     */
    public DigestResult getDigestSingle(FileInputStream stream) throws IOException {
        int bytesRead = 0;
        byte[] buf = new byte[BLOCK_READ_SIZE];
        do {
            bytesRead = stream.read(buf, 0, BLOCK_READ_SIZE);
            if (bytesRead > 0) {
                for (MessageDigest digest: digests) {
                    digest.update(buf, 0, bytesRead);
                }
            }
        } while (bytesRead > 0);

        return finalizeDigestResult();
    }

    /**
     * Compute the digest(s) for the contents of the file.
     *
     * @param stream the stream to read.
     * @return the resulting digests.
     * @throws IOException when things go wrong with the IO.
     */
    public DigestResult getDigestMulti(FileInputStream stream) throws IOException {
        reset();

        try {
            Thread fileReaderThread = new Thread(new FileReaderThread(stream));
            Thread digestComputerThread = new Thread(new DigestComputerThread(digests));

            fileReaderThread.start();
            digestComputerThread.start();

            fileReaderThread.join();
            digestComputerThread.join();

            return finalizeDigestResult();
        } catch (InterruptedException ex) {
            log.log(Level.WARNING, "Execution was interrupted. Results are unreliable", ex);
        } finally {
            stream.close();
        }
        return null;
    }

    public void reset() {
        for (MessageDigest digest : digests) {
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

    class FileReaderThread implements Runnable {

        private FileInputStream stream;
        private FileChannel channel;

        public FileReaderThread(FileInputStream stream) {
            this.stream = stream;
            this.channel = stream.getChannel();
        }

        public void run() {
            try {
                long remaining = channel.size();
                long offset = 0;
                while (remaining > 0) {
                    long bytesToRead = Math.min(BLOCK_READ_SIZE, remaining);
                    MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, offset, bytesToRead);
                    buffer.load();
                    ByteBuffer buf = exchanger.exchange(buffer);
                    offset += bytesToRead;
                    remaining -= bytesToRead;
                }
                // Notify the digesting thread that we're finished.
                exchanger.exchange(null);
            } catch (InterruptedException ex) {
                Logger.getLogger(FileHasher.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
            }
        }
    }

    class DigestComputerThread implements Runnable {

        Collection<MessageDigest> digests;

        public DigestComputerThread(Collection<MessageDigest> digests) {
            this.digests = digests;
        }

        public void run() {
            try {
                while (true) {
                    ByteBuffer buf = exchanger.exchange(null);
                    if (buf == null) {
                        break;
                    }
                    for (MessageDigest digest : digests) {
                        digest.update(buf);
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(FileHasher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
