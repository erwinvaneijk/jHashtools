/*
 * Copyright (c) 2012 Erwin van Eijk <erwin.vaneijk@gmail.com>.
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

package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;

import nl.minjus.nfi.dt.jhashtools.AccessTimes;
import nl.minjus.nfi.dt.jhashtools.DigestResult;

/**
 * Transport for the Digests.
 *
 * @author Erwin van Eijk
 *
 */
public class DigestMessage
{

    private final File file;

    private final DigestResult digest;

    private final AccessTimes accessTimes;

    public DigestMessage(final File file, final DigestResult digest, final AccessTimes accessTimes)
    {
        this.digest = digest;
        this.file = file;
        this.accessTimes = accessTimes;
    }

    /**
     * Get the file in this message.
     *
     * @return the filename
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Get the digest of the file.
     *
     * @return a digest
     */
    public DigestResult getDigestResult() {
        return this.digest;
    }

    /**
     * Get the access times observed while hashing the file.
     *
     * @return the observed access times
     */
    public AccessTimes getAccessTimes() {
        return this.accessTimes;
    }
}
