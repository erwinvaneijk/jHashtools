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

package nl.minjus.nfi.dt.jhashtools;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import nl.minjus.nfi.dt.jhashtools.exceptions.AlgorithmNotFoundException;
import nl.minjus.nfi.dt.jhashtools.exceptions.NoMatchingAlgorithmsError;
import nl.minjus.nfi.dt.jhashtools.hashers.DigestMask;

/**
 * @author Erwin van Eijk
 */
public class DigestResult extends TreeSet<Digest>
{
    /**
     * Generated serial id.
     */
    private static final long serialVersionUID = 8846362968322773564L;

    /**
     * The mask that indicates the digests that are supported.
     */
    private int containedDigestMask;

    public DigestResult()
    {
        this.containedDigestMask = 0x0000;
    }

    public DigestResult(final Digest value)
    {
        this.containedDigestMask = 0x0000;
        this.add(value);
    }

    public DigestResult(final Collection<? extends Object> coll)
    {
        for (final Object d : coll) {
            if (d instanceof MessageDigest) {
                this.add(new Digest((MessageDigest) d));
            } else if (d instanceof Digest) {
                this.add((Digest) d);
            }
        }
    }

    @Override
    public boolean add(final Digest value) {
        containedDigestMask = DigestResult.updateAlgorithmMask(containedDigestMask, value.getAlgorithm());
        return super.add(value);
    }

    public Collection<String> getAlgorithms() {
        final Collection<String> coll = new ArrayList<String>();
        for (final Digest d : this) {
            coll.add(d.getAlgorithm());
        }
        return coll;
    }

    public boolean containsResult(final String key) {
        for (final Digest e : this) {
            if (e.getAlgorithm().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public Digest getDigest(final String key) {
        for (final Digest e : this) {
            if (e.getAlgorithm().equals(key)) {
                return e;
            }
        }
        throw new AlgorithmNotFoundException("Algorithm " + key + " not found");
    }

    public String getHexDigest(final String digestName) {
        return this.getDigest(digestName).toHex();
    }

    public void setDigest(final Digest value) {
        this.add(value);
    }

    public Digest digest() {
        return this.iterator().next();
    }

    @Override
    public boolean equals(final Object o) {
        if ((o != null) && (o instanceof DigestResult)) {
            final DigestResult other = (DigestResult) o;
            for (final Digest d : this) {
                if (!other.contains(d)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * This is to shut CheckStyle up because we implement equals.
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * matches returns true if the digests available on both arguments (this) and (o) are equal. If a digest algorithm
     * on one side is available, that is not available on the other it is not compared.
     * <p/>
     * When there is no match in algorithm, matches returns false.
     *
     * @param other
     *            the other side to compare to.
     *
     * @return true when this matches other.
     *
     * @throws NoMatchingAlgorithmsError
     *             when there are no matching algorithms between this and other.
     */
    public boolean matches(final DigestResult other) {
        if (other != null) {
            final int matchingAlgorithms = this.containedDigestMask & other.containedDigestMask;
            if (matchingAlgorithms == 0) {
                throw new NoMatchingAlgorithmsError();
            }
            final List<String> algorithms = DigestMask.getInstance().contains((short) matchingAlgorithms);
            boolean correct = true; // Everything is true for the empty set of
                                    // checked results.
            for (final String algorithm : algorithms) {
                final Digest d1 = this.getDigest(algorithm);
                final Digest d2 = other.getDigest(algorithm);
                correct = correct && d1.equals(d2);
            }
            return correct;
        }
        return false;
    }

    private static int updateAlgorithmMask(final int mask, final String algorithm) {
        return mask | DigestMask.getInstance().getMask(algorithm);
    }
}
