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
import java.security.Security;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
            MASKS.put("ripemd160", RIPEMD_ALGORITHM_MASK);
            MASKS.put("crc", CRC_ALGORITHM);

            REVERSE_MASKS.put(MD5_ALGORITHM, MessageDigest.getInstance("md5").getAlgorithm());
            REVERSE_MASKS.put(SHA1_ALGORITHM_MASK, MessageDigest.getInstance("sha-1").getAlgorithm());
            REVERSE_MASKS.put(SHA256_ALGORITHM_MASK, MessageDigest.getInstance("sha-256").getAlgorithm());
            REVERSE_MASKS.put(SHA384_ALGORITHM_MASK, MessageDigest.getInstance("sha-384").getAlgorithm());
            REVERSE_MASKS.put(SHA512_ALGORITHM_MASK, MessageDigest.getInstance("sha-512").getAlgorithm());
            REVERSE_MASKS.put(RIPEMD_ALGORITHM_MASK, "ripemd160");
            REVERSE_MASKS.put(CRC_ALGORITHM, "crc");
        } catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Pattern to not use the double check idiom for singeltons. This
     * is the thread safe alternative.
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
            result = MASKS.get(algorithm.toLowerCase(Locale.ROOT));
            if (result == null) {
                synchronized (LOCK) {
                    result = MASKS.get(algorithm.toLowerCase(Locale.ROOT));
                    if (result == null) {
                        if (maxValue <= 0) {
                            throw new IllegalStateException("No digest mask left to register algorithm " + algorithm);
                        }
                        // Register under the key used for lookups, so the algorithm keeps this mask (issue #30).
                        MASKS.put(algorithm.toLowerCase(Locale.ROOT), maxValue);
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
