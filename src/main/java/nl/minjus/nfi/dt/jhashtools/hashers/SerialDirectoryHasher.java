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
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 * @author Erwin van Eijk
 */
class SerialDirectoryHasher extends AbstractDirectoryHasher
{
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentFileHasher.class);

    private final DirHasherResult results = new DirHasherResult();

    public SerialDirectoryHasher()
    {
        super();
    }

    public SerialDirectoryHasher(final String algorithm) throws NoSuchAlgorithmException
    {
        super(algorithm);
    }

    public SerialDirectoryHasher(final Collection<String> algorithms) throws NoSuchAlgorithmException
    {
        super(algorithms);
    }

    @Override
    public DirHasherResult getDigests(final File startFile) {
        this.results.clear();
        updateDigests(this.results, startFile);
        return this.results;
    }

    @Override
    public void updateDigests(final DirHasherResult digests, final File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("File %s does not exist", file.toString()));
        }

        try {
            final FileWalker walker = new FileWalker();
            final DirectoryVisitor visitor = new DirectoryVisitor(getTheAlgorithms(), digests);
            visitor.setVerbose(this.isVerbose());
            walker.addWalkerVisitor(visitor);
            walker.walk(file);
        } catch (final NoSuchAlgorithmException ex) {
            LOG.error("This is not possible: Algorithm not found", ex);
        }
    }
}
