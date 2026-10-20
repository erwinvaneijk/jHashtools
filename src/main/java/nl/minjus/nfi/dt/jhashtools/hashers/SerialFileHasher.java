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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            return getDigest(inputStream);
        }
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
