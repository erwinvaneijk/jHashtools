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

import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import nl.minjus.nfi.dt.jhashtools.hashers.actors.ActingDirectoryHasher;

/**
 * This utility class knows how to create new instances of DirectoryHasher's.
 *
 * @author Erwin van Eijk
 */
public class DirectoryHasherCreator
{

    /**
     * Make it impossible to create an instance of this class.
     */
    protected DirectoryHasherCreator()
    {
    }

    public static DirectoryHasher create(final ConcurrencyMode concurrencyMode) {
        if (concurrencyMode == ConcurrencyMode.SINGLE) {
            return new SerialDirectoryHasher();
        } else {
            try {
                return new ActingDirectoryHasher();
            } catch (final NoSuchAlgorithmException ex) {
                return null;
            }
        }
    }

    public static DirectoryHasher create(final ConcurrencyMode concurrencyMode, final Collection<String> digests)
        throws NoSuchAlgorithmException
    {
        if (concurrencyMode == ConcurrencyMode.SINGLE) {
            return new SerialDirectoryHasher(digests);
        } else {
            return new ActingDirectoryHasher(digests);
        }
    }
}
