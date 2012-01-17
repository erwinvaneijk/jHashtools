/*
 * Copyright (c) 2010 Erwin van Eijk.  All rights reserved.
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
