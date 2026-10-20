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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import nl.minjus.nfi.dt.jhashtools.utils.StringOperations;

/**
 * This class holds some convenience for handling the results delivered by MessageDigest#digest.
 *
 * @author Erwin van Eijk
 */
public class Digest implements Comparable<Digest>
{

    /**
     * The content of the digest.
     */
    private byte[] content;
    /**
     * The name of the algorithm.
     */
    private String algorithm;
    /**
     * Group hexadecimal nibbles in this size.
     */
    private static final int HEX_GROUP_SIZE = 4;

    /**
     * Constructor.
     *
     * @param anAlgorithm
     *            the Algorithm to store.
     * @param aValue
     *            the Digest aValue.
     */
    public Digest(final String anAlgorithm, final byte[] aValue)
    {
        this.content = aValue;
        this.algorithm = anAlgorithm;
    }

    /**
     * Constructor.
     *
     * @param anAlgorithm
     *            the Algorithm to use.
     * @param aHexValue
     *            the Digest value, as hexadecimal characters.
     */
    public Digest(final String anAlgorithm, final String aHexValue)
    {
        this.algorithm = anAlgorithm;
        this.content = StringOperations.hexToBytes(aHexValue);
    }

    /**
     * Constructor. This is a special case where the expected format is like 'digestname:value'.
     *
     * @param aValue
     *            the value to parse.
     */
    // The input is "<algorithm>:<hex>"; String.split's handling of empty parts does not matter here.
    @SuppressWarnings("StringSplitter")
    @JsonCreator
    public Digest(final String aValue)
    {
        final String[] parts = aValue.split(":");
        this.algorithm = parts[0];
        this.content = StringOperations.hexToBytes(parts[1]);
    }

    /**
     * Constructor.
     *
     * @param digest
     */
    public Digest(final MessageDigest digest)
    {
        // JDK digests keep the requested (lower-case) name, BouncyCastle reports its own (e.g. "RIPEMD160").
        this.algorithm = digest.getAlgorithm().toLowerCase(Locale.ROOT);
        this.content = digest.digest();
    }

    /**
     * Setter for the algorithm value.
     *
     * @param anAlgorithm
     *            the algorithm to set.
     */
    public void setAlgorithm(final String anAlgorithm) {
        this.algorithm = anAlgorithm;
    }

    /**
     * Get the algorithm vale.
     *
     * @return the algorithm
     */
    public String getAlgorithm() {
        return this.algorithm;
    }

    /**
     * Set the digest value.
     *
     * @param aValue
     *            the value to use.
     */
    public void setContent(final byte[] aValue) {
        this.content = aValue;
    }

    /**
     * Get the value.
     *
     * @return the value of the digest.
     */
    public byte[] getContent() {
        return this.content;
    }

    /**
     * Convert the value to hex.
     *
     * @return a hexadecimal representation of the digest value.
     */
    public String toHex() {
        return StringOperations.hexify(content);
    }

    /**
     * Output the algorithm and value as one whole.
     *
     * @return a string.
     */
    @Override
    @JsonValue
    public String toString() {
        return this.toString(':');
    }

    /**
     * Output the algorithm and value as one whole, separated by joinChar.
     *
     * @param aJoinChar
     *            the character to join algorithm and value.
     * @return a string.
     */
    public String toString(final char aJoinChar) {
        return this.algorithm + aJoinChar + StringOperations.hexify(content);
    }

    /**
     * Get a pretty representation of the algorithm and the value of the digest.
     *
     * @param theJoinChar
     *            the character to join algorithms and value.
     * @return a string.
     */
    public String prettyPrint(final char theJoinChar) {
        return this.algorithm + theJoinChar
            + StringOperations.split(StringOperations.hexify(content), HEX_GROUP_SIZE);
    }

    /**
     * Get a pretty representation of the algorithm and the value of the digest.
     *
     * @param theJoinChar
     *            the character to join algorithms and value.
     * @return a string.
     */
    public String prettyPrint(final String theJoinChar) {
        return this.algorithm + theJoinChar
            + StringOperations.split(StringOperations.hexify(content), HEX_GROUP_SIZE);
    }

    /**
     * Implement equals.
     *
     * @param theOther
     *            the other value to compare to.
     * @return true if theOther and this have the same algorithm and the same value.
     */
    @Override
    public boolean equals(final Object theOther) {
        if (this == theOther) {
            return true;
        }

        if ((theOther != null) && (theOther instanceof Digest)) {
            final Digest digest = (Digest) theOther;
            return (this.algorithm.equals(digest.algorithm) && (Arrays.equals(this.content, digest.content)));
        } else {
            return false;
        }
    }

    /**
     * Needed when you implement equals.
     */
    @Override
    public int hashCode() {
        return this.algorithm.hashCode() + Arrays.hashCode(this.content);
    }

    /**
     * Compare this to the other.
     *
     * @param anOther
     *            the compare to.
     * @return an int.
     */
    @Override
    public int compareTo(final Digest anOther) {
        if (!this.algorithm.equals(anOther.algorithm)) {
            return this.algorithm.compareTo(anOther.algorithm);
        }

        for (int i = 0; i < Math.min(this.content.length, anOther.content.length); i++) {
            if (this.content[i] < anOther.content[i]) {
                return 1;
            } else if (this.content[i] > anOther.content[i]) {
                return -1;
            }
        }
        if (this.content.length == anOther.content.length) {
            return 0;
        } else {
            return (this.content.length - anOther.content.length);
        }
    }

}
