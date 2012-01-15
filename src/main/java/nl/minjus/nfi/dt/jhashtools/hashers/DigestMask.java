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
import java.security.Security;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Where all the digests are managed.
 */
public class DigestMask
{
    private static final int MAXIMUM_SUPPORTED_VALUE = 0x0080;
    private static final Object LOCK = new Object();

    private static final Map<String, Short> MASKS = new HashMap<String, Short>();
    private static final Map<Short, String> REVERSE_MASKS = new HashMap<Short, String>();
    private static final short MD5_ALGORITHM = 0x0001;
    private static final short SHA1_ALGORITHM_MASK = 0x0002;
    private static final short SHA256_ALGORITHM_MASK = 0x0004;
    private static final short SHA384_ALGORITHM_MASK = 0x0008;
    private static final short SHA512_ALGORITHM_MASK = 0x0010;
    private static final short RIPEMD_ALGORITHM_MASK = 0x0020;
    private static final short CRC_ALGORITHM = 0x0040;
    private static short maxValue = MAXIMUM_SUPPORTED_VALUE;

    static {
        try {
            Security.addProvider(new BouncyCastleProvider());

            MASKS.put(MessageDigest.getInstance("md5").getAlgorithm(), MD5_ALGORITHM);
            MASKS.put(MessageDigest.getInstance("sha-1").getAlgorithm(), SHA1_ALGORITHM_MASK);
            MASKS.put("sha-1", SHA1_ALGORITHM_MASK);
            MASKS.put(MessageDigest.getInstance("sha-256").getAlgorithm(), SHA256_ALGORITHM_MASK);
            MASKS.put(MessageDigest.getInstance("sha-384").getAlgorithm(), SHA384_ALGORITHM_MASK);
            MASKS.put(MessageDigest.getInstance("sha-512").getAlgorithm(), SHA512_ALGORITHM_MASK);
            MASKS.put(MessageDigest.getInstance("ripemd160").getAlgorithm(), RIPEMD_ALGORITHM_MASK);
            MASKS.put("crc", CRC_ALGORITHM);

            REVERSE_MASKS.put(MD5_ALGORITHM, MessageDigest.getInstance("md5").getAlgorithm());
            REVERSE_MASKS.put(SHA1_ALGORITHM_MASK, MessageDigest.getInstance("sha-1").getAlgorithm());
            REVERSE_MASKS.put(SHA256_ALGORITHM_MASK, MessageDigest.getInstance("sha-256").getAlgorithm());
            REVERSE_MASKS.put(SHA384_ALGORITHM_MASK, MessageDigest.getInstance("sha-384").getAlgorithm());
            REVERSE_MASKS.put(SHA512_ALGORITHM_MASK, MessageDigest.getInstance("sha-512").getAlgorithm());
            REVERSE_MASKS.put(RIPEMD_ALGORITHM_MASK, MessageDigest.getInstance("ripemd160").getAlgorithm());
            REVERSE_MASKS.put(CRC_ALGORITHM, "crc");
        } catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Pattern to not use the double check idiom for singeltons. This
     * is the threadsafe alternative.
     *
     * @author Erwin van Eijk
     *
     */
    private static class SingletonHolder
    {
        static final DigestMask INSTANCE = new DigestMask();
    }

    public static final DigestMask getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public final Short getMask(final String algorithm) {
        if (algorithm == null) {
            return 0;
        }

        Short result = MASKS.get(algorithm);
        if (result == null) {
            result = MASKS.get(algorithm.toLowerCase());
            if (result == null) {
                synchronized (LOCK) {
                    result = MASKS.get(algorithm.toLowerCase());
                    if (result == null) {
                        MASKS.put(algorithm.toUpperCase(), maxValue);
                        REVERSE_MASKS.put(maxValue, algorithm);
                        final short returnValue = maxValue;
                        maxValue = (short) (maxValue << 1);
                        return returnValue;
                    }
                }
            }
        }
        return result;
    }

    public final List<String> contains(final short mask) {
        final List<String> result = new LinkedList<String>();
        int i = 1;
        if (mask == 0) {
            return result;
        }
        while (i < maxValue) {
            if ((mask & i) != 0x0000) {
                result.add(REVERSE_MASKS.get((short) i));
            }
            i = i << 1;
        }
        return result;
    }
}
