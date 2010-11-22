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

import nl.minjus.nfi.dt.jhashtools.exceptions.AlgorithmNotFoundException;
import nl.minjus.nfi.dt.jhashtools.exceptions.NoMatchingAlgorithmsError;
import nl.minjus.nfi.dt.jhashtools.hashers.DigestMask;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * @author Erwin van Eijk
 */
public class DigestResult extends TreeSet<Digest>
{
    private int containedDigestMask = 0;

    public DigestResult()
    {
    }

    public DigestResult(Digest value)
    {
        this.add(value);
    }

    public DigestResult(Collection<? extends Object> coll)
    {
        for (Object d : coll) {
            if (d instanceof MessageDigest) {
                this.add(new Digest((MessageDigest) d));
            } else if (d instanceof Digest) {
                this.add((Digest) d);
            }
        }
    }

    public boolean add(Digest value) {
        containedDigestMask = this.updateAlgorithmMask(containedDigestMask, value.getAlgorithm());
        return super.add(value);
    }

    public Collection<String> getAlgorithms()
    {
        Collection<String> coll = new ArrayList<String>();
        for (Digest d : this) {
            coll.add(d.getAlgorithm());
        }
        return coll;
    }

    public boolean containsResult(String key)
    {
        for (Digest e : this) {
            if (e.getAlgorithm().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public Digest getDigest(String key)
    {
        for (Digest e : this) {
            if (e.getAlgorithm().equals(key)) {
                return e;
            }
        }
        throw new AlgorithmNotFoundException("Algorithm " + key + " not found");
    }

    public String getHexDigest(String digestName)
    {
        return this.getDigest(digestName).toHex();
    }

    public void setDigest(Digest value)
    {
        this.add(value);
    }

    public Digest digest()
    {
        return this.iterator().next();
    }

    @Override
    public boolean equals(Object o)
    {
        if ((o != null) && (o instanceof DigestResult)) {
            DigestResult other = (DigestResult) o;
            for (Digest d : this) {
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
     * matches returns true if the digests available on both arguments (this) and (o) are equal. If a digest algorithm
     * on one side is available, that is not available on the other it is not compared.
     * <p/>
     * When there is no match in algorithm, matches returns false.
     *
     * @param other the other side to compare to.
     *
     * @return true when this matches other.
     *
     * @throws NoMatchingAlgorithmsError when there are no matching algorithms between this and other.
     */
    public boolean matches(DigestResult other)
    {
        if (other != null) {
            int matchingAlgorithms = this.containedDigestMask & other.containedDigestMask;
            if (matchingAlgorithms == 0) {
                throw new NoMatchingAlgorithmsError();
            }
            List<String> algorithms = DigestMask.getInstance().contains((short) matchingAlgorithms);
            boolean correct = true;         // Everything is true for the empty set of checked results.
            for (String algorithm: algorithms) {
                Digest d1 = this.getDigest(algorithm);
                Digest d2 = other.getDigest(algorithm);
                correct = correct && d1.equals(d2);
            }
            return correct;
        }
        return false;
    }

    private static int updateAlgorithmMask(int mask, String algorithm) {
        return mask | DigestMask.getInstance().getMask(algorithm);
    }
}
