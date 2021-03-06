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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import nl.minjus.nfi.dt.jhashtools.DigestResult;

/**
 * This class computes digests of files without using multi threading.
 */
class SerialFileHasher extends AbstractFileHasher
{

    public SerialFileHasher()
    {
        super();
    }

    public SerialFileHasher(final DigestAlgorithm digest) throws NoSuchAlgorithmException
    {
        super(digest);
    }

    public SerialFileHasher(final Collection<DigestAlgorithm> digests) throws NoSuchAlgorithmException
    {
        super(digests);
    }

    /**
     * Compute the digest(s) for the contents of the file.
     *
     * @param file
     *            the File to compute the digests for.
     *
     * @return a DigestResult containing the result of the computation.
     *
     * @throws java.io.IOException
     *             thrown when some IOException occurs.
     */
    @Override
    public DigestResult getDigest(final File file) throws IOException {
        if (file == null) {
            return null;
        }

        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        final FileInputStream inputStream = new FileInputStream(file);
        return getDigest(inputStream);
    }

    /**
     * Compute the digest(s) for the contents of the file.
     *
     * @param stream
     *            the stream to read.
     *
     * @return the resulting digests.
     *
     * @throws java.io.IOException
     *             when things go wrong with the IO.
     */
    @Override
    public DigestResult getDigest(final InputStream stream) throws IOException {
        final Collection<MessageDigest> digestInstances = this.getMessageDigests();
        final byte[] buf = new byte[BLOCK_READ_SIZE];
        int bytesRead;
        bytesRead = stream.read(buf, 0, BLOCK_READ_SIZE);
        while (bytesRead > 0) {
            for (final MessageDigest digest : digestInstances) {
                digest.update(buf, 0, bytesRead);
            }
            bytesRead = stream.read(buf, 0, BLOCK_READ_SIZE);
        }
        return new DigestResult(digestInstances);
    }
}
