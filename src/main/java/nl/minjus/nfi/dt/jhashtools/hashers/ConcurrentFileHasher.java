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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.minjus.nfi.dt.jhashtools.DigestResult;

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

    public ConcurrentFileHasher(final DigestAlgorithm digest) throws NoSuchAlgorithmException
    {
        super(digest);
        this.exchanger = new Exchanger<ByteBuffer>();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public ConcurrentFileHasher(final ExecutorService service)
    {
        this.exchanger = new Exchanger<ByteBuffer>();
        this.executorService = service;
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
