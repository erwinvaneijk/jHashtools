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

package nl.minjus.nfi.dt.jhashtools.utils;

/**
 * @author Erwin van Eijk
 */
public final class StringOperations
{

    private static final int UNSIGNED_BYTE_MAX_VALUE = 256;
    private static final int HIGH_NIBBLE_SHIFT = 4;
    private static final int HIGH_NIBBLE_MASK = 0xF0;
    private static final int LOW_NIBBLE_MASK = 0x0F;
    private static final String HEXES = "0123456789abcdef";

    /**
     * Create a base-16 encoding of the given raw bytes.
     *
     * @param aRawValue
     *            the value to encode.
     * @return a String.
     */
    public static String hexify(final byte[] aRawValue) {
        if (aRawValue == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * aRawValue.length);
        for (final byte b : aRawValue) {
            hex.append(HEXES.charAt((b & HIGH_NIBBLE_MASK) >> HIGH_NIBBLE_SHIFT))
                .append(HEXES.charAt((b & LOW_NIBBLE_MASK)));
        }
        return hex.toString();
    }

    /**
     * Convert the hex values to real-life bytes.
     *
     * @param hex
     *            the values to convert.
     * @return a byte array.
     */
    private static byte[] hexToBytes(final char[] hex) {
        final int length = hex.length / 2;
        final byte[] raw = new byte[length];
        for (int i = 0; i < length; i++) {
            final int high = Character.digit(hex[i * 2], 16);
            final int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << HIGH_NIBBLE_SHIFT) | low;
            if (value > Byte.MAX_VALUE) {
                value -= UNSIGNED_BYTE_MAX_VALUE;
            }
            raw[i] = (byte) value;
        }
        return raw;
    }

    /**
     * Convert a base-16 encoded String into the binary version.
     *
     * @param aHexString
     *            the String to convert.
     * @return the binary version.
     */
    public static byte[] hexToBytes(final String aHexString) {
        return hexToBytes(aHexString.toCharArray());
    }

    public static String split(final String aString, final int theSplitSize) {
        final StringBuilder builder = new StringBuilder(aString.length() + (aString.length() / theSplitSize));
        int i = 0;
        while (i < aString.length()) {
            builder.append(aString.charAt(i));
            i++;
            if (((i % theSplitSize) == 0) && (i < aString.length())) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }
}
