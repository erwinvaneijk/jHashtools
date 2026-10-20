/*
 * Copyright (c) 2010 Erwin van Eijk.
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A holder for the name of the algorithm and the method to actually create
 * a corresponding digest value.
 */
public class DigestAlgorithm
{
    private final String name;
    private final short value;

    public DigestAlgorithm(final String name, final short value)
    {
        this.name = name;
        this.value = value;
    }

    public DigestAlgorithm(final String name)
    {
        this.name = name;
        this.value = DigestMask.getInstance().getMask(name);
    }

    /**
     * Get the name of the algorithm.
     *
     * @return the name
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Get the shorthand value for the algorithm.
     *
     * @return the value
     */
    public final short getValue() {
        return this.value;
    }

    /**
     * Get the real algorithm for this named algorithm.
     *
     * @return the algorithm
     *          the algorithm that can be used to compute digest values.
     * @throws NoSuchAlgorithmException
     *          when the named algorithm does not exist.
     */
    public synchronized MessageDigest getInstance() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(this.name);
    }
}
