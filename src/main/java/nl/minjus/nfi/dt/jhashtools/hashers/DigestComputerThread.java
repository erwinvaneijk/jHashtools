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

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Exchanger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;

/**
 * This callable can be use to create a thread that is able to compute digests in a separate thread.
 *
 * @author Erwin van Eijk
 */
class DigestComputerThread implements Callable<DigestResult>
{
    private static final Logger LOG = LoggerFactory.getLogger(DigestComputerThread.class);
    private final Collection<DigestAlgorithm> digests;
    private final Exchanger<ByteBuffer> exchanger;

    /**
     * Constructor.
     *
     * @param digests
     *            the digests to use.
     * @param exchanger
     *            the exchanger to get the data from.
     */
    public DigestComputerThread(final Collection<DigestAlgorithm> digests,
        final Exchanger<ByteBuffer> exchanger)
    {
        this.digests = digests;
        this.exchanger = exchanger;
    }

    /**
     * The entry point for the thread.
     *
     * @see Callable#call
     *
     * @return a DigestResult instance.
     */
    @Override
    public DigestResult call() {
        final Collection<MessageDigest> digests = new ArrayList<MessageDigest>(this.digests.size());
        for (final DigestAlgorithm alg : this.digests) {
            try {
                digests.add(alg.getInstance());
            } catch (final NoSuchAlgorithmException ex) {
                // FIXME:
                // Decide whether we should log this properly.
                // For now, we ignore.
                LOG.error("Algorithm not supported: " + alg.getName());
            }
        }
        try {
            final byte[] buf = new byte[AbstractFileHasher.BLOCK_READ_SIZE];
            ByteBuffer buffer = ByteBuffer.wrap(buf, 0, AbstractFileHasher.BLOCK_READ_SIZE);
            while (true) {
                buffer = this.exchanger.exchange(buffer);
                if (buffer == null) {
                    break;
                }
                final int savePos = buffer.position();
                for (final MessageDigest digest : digests) {
                    digest.update(buffer);
                    buffer.position(savePos); // Reset the position.
                }
            }
            final DigestResult res = new DigestResult();
            for (final MessageDigest digest : digests) {
                res.add(new Digest(digest));
            }
            return res;
        } catch (final InterruptedException ex) {
            LOG.error("Execution was aborted.", ex);
            return null;
        }
    }
}
