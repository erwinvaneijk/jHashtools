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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import nl.minjus.nfi.dt.jhashtools.DigestResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compute file hashes with multiple threads.
 *
 * @author Erwin van Eijk
 */
public class ConcurrentFileHasher extends AbstractFileHasher
{
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentFileHasher.class.getName());

    private final ExecutorService executorService;
    private final Exchanger<ByteBuffer> exchanger;

    public ConcurrentFileHasher()
    {
        super();
        this.executorService = Executors.newSingleThreadExecutor();
        this.exchanger = new Exchanger<ByteBuffer>();
    }

    public ConcurrentFileHasher(final DigestAlgorithm digest) throws NoSuchAlgorithmException
    {
        super(digest);
        this.exchanger = new Exchanger<ByteBuffer>();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public ConcurrentFileHasher(final Collection<DigestAlgorithm> digests) throws NoSuchAlgorithmException
    {
        super(digests);
        this.exchanger = new Exchanger<ByteBuffer>();
        this.executorService = Executors.newFixedThreadPool(2);
    }

    /**
     * Compute the digest(s) for the contents of the file.
     *
     * @param file
     *            the File to compute the digests for.
     *
     * @return a DigestResult containing the result of the computation.
     *
     * @throws java.io.FileNotFoundException
     *             thrown when <c>file<c> doesn't exist.
     * @throws java.io.IOException
     *             thrown when some IOException occurs.
     */
    @Override
    public final DigestResult getDigest(final File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("File %s does not exist", file.toString()));
        }

        final FileInputStream stream = new FileInputStream(file);
        try {
            return getDigest(stream);
        } finally {
            stream.close();
        }
    }

    @Override
    public DigestResult getDigest(final InputStream stream) throws IOException {
        try {
            final Future<DigestResult> digestComputerThread = executorService
                .submit(new DigestComputerThread(this.getDigests(), this.exchanger));

            int bytesRead;
            byte[] buf = new byte[BLOCK_READ_SIZE];
            ByteBuffer buffer = ByteBuffer.wrap(buf);
            do {
                bytesRead = stream.read(buf, 0, BLOCK_READ_SIZE);
                if (bytesRead > 0) {
                    if (bytesRead != BLOCK_READ_SIZE) {
                        buffer.limit(bytesRead);
                    }
                    buffer = exchanger.exchange(buffer);
                    buf = buffer.array();
                }
            } while (bytesRead > 0);

            // Notify the digesting thread that we're finished.
            exchanger.exchange(null);
            return digestComputerThread.get();
        } catch (final InterruptedException ex) {
            LOG.error("", ex);
        } catch (final ExecutionException ex) {
            LOG.error("", ex);
        }
        return null;
    }
}
