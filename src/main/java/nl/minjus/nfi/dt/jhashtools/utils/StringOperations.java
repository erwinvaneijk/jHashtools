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

    private static final String HEXES = "0123456789abcdef";

    /**
     * Create a base-16 encoding of the given raw bytes.
     *
     * @param aRawValue the value to encode.
     * @return a String.
     */
    public static String hexify(final byte[] aRawValue)
    {
        if (aRawValue == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * aRawValue.length);
        for (final byte b : aRawValue) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    /**
     * Convert the hex values to real-life bytes.
     *
     * @param hex the values to convert.
     * @return a byte array.
     */
    private static byte[] hexToBytes(char[] hex)
    {
        int length = hex.length / 2;
        byte[] raw = new byte[length];
        for (int i = 0; i < length; i++) {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << 4) | low;
            if (value > 127) {
                value -= 256;
            }
            raw[i] = (byte) value;
        }
        return raw;
    }

    /**
     * Convert a base-16 encoded String into the binary version.
     * @param aHexString the String to convert.
     * @return the binary version.
     */
    public static byte[] hexToBytes(String aHexString)
    {
        return hexToBytes(aHexString.toCharArray());
    }

    public static String split(String aString, int theSplitSize)
    {
        StringBuilder builder = new StringBuilder(aString.length() + (aString.length() / 4));
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
