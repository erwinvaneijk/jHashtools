/*
 * Copyright (c) 2010 Erwin van Eijk. All rights reserved.
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

import nl.minjus.nfi.dt.jhashtools.DigestResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.concurrent.Exchanger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConcurrentFileHasher extends AbstractFileHasher {
    private final static Logger log = Logger.getLogger(ConcurrentFileHasher.class.getName());

    private Exchanger<ByteBuffer> exchanger;

    public ConcurrentFileHasher() {
        super();
        this.exchanger = new Exchanger<ByteBuffer>();
    }

    public ConcurrentFileHasher(MessageDigest digest) {
        super(digest);
        this.exchanger = new Exchanger<ByteBuffer>();
    }

    public ConcurrentFileHasher(Collection<MessageDigest> digests) {
        super(digests);
        this.exchanger = new Exchanger<ByteBuffer>();
    }
    /**
     * Compute the digest(s) for the contents of the file.
     *
     * @param file the File to compute the digests for.
     *
     * @return a DigestResult containing the result of the computation.
     *
     * @throws java.io.FileNotFoundException thrown when <c>file</c> doesn't exist.
     * @throws java.io.IOException           thrown when some IOException occurs.
     */
    public DigestResult getDigest(File file) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("File %s does not exist", file.toString()));
        }

        FileInputStream stream = new FileInputStream(file);
        try {
            Thread digestComputerThread = new Thread(new DigestComputerThread(digests));

            digestComputerThread.start();

            try {
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
            } catch (InterruptedException ex) {
                log.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Cannot read anymore. Whoops.", ex);
            }

            digestComputerThread.join();

            return super.finalizeDigestResult();
        } catch (InterruptedException ex) {
            log.log(Level.WARNING, "Execution was interrupted. Results are unreliable", ex);
        } finally {
            stream.close();
        }
        return null;
    }

    class DigestComputerThread implements Runnable {

        Collection<MessageDigest> digests;

        public DigestComputerThread(Collection<MessageDigest> digests) {
            this.digests = digests;
        }

        public void run() {

            try {
                byte[] buf = new byte[BLOCK_READ_SIZE];
                ByteBuffer buffer = ByteBuffer.wrap(buf, 0, BLOCK_READ_SIZE);
                while (true) {
                    buffer = exchanger.exchange(buffer);
                    if (buffer == null) {
                        break;
                    }
                    for (MessageDigest digest : digests) {
                        digest.update(buffer);
                    }
                }
            } catch (InterruptedException ex) {
                log.log(Level.SEVERE, "Execution was aborted.", ex);
            }
        }
    }
}