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
