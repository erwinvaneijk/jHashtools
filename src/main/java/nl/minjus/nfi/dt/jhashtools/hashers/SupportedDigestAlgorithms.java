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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author Erwin van Eijk
 */
class SupportedDigestAlgorithms
{
    private static Map<String, Boolean> supportedMap = new HashMap<String, Boolean>();

    static {
        // BouncyCastle provides ripemd160. Register it here as well as in DigestMask, so the answer does not depend
        // on which of the two classes is initialised first. Adding an already installed provider has no effect.
        Security.addProvider(new BouncyCastleProvider());
    }

    public static boolean isSupportedAlgorithm(final String algorithm) {
        try {
            final Boolean cachedValue = supportedMap.get(algorithm);
            if (cachedValue != null) {
                return cachedValue;
            } else {
                final MessageDigest digest = MessageDigest.getInstance(algorithm);
                supportedMap.put(digest.getAlgorithm(), true);
                return true;
            }
        } catch (final NoSuchAlgorithmException ex) {
            supportedMap.put(algorithm, false);
            return false;
        }
    }
}
