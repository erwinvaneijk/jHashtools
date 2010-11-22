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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools;

import nl.minjus.nfi.dt.jhashtools.utils.StringOperations;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

import java.security.MessageDigest;
import java.util.*;

/**
 * This class holds some convenience for handling the results delivered
 * by MessageDigest#digest.
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
     * @param anAlgorithm the Algorithm to store.
     * @param aValue the Digest aValue.
     */
    public Digest(String anAlgorithm, byte[] aValue)
    {
        this.content = aValue;
        this.algorithm = anAlgorithm;
    }

    /**
     * Constructor.
     *
     * @param anAlgorithm the Algorithm to use.
     * @param aHexValue the Digest value, as hexadecimal characters.
     */
    public Digest(String anAlgorithm, String aHexValue)
    {
        this.algorithm = anAlgorithm;
        this.content = StringOperations.hexToBytes(aHexValue);
    }

    /**
     * Constructor. This is a special case where the expected format is like 'digestname:value'.
     *
     * @param aValue the value to parse.
     */
    @JsonCreator
    public Digest(String aValue)
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
    public Digest(MessageDigest digest)
    {
        this.algorithm = digest.getAlgorithm();
        this.content = digest.digest();
    }

    /**
     * Setter for the algorithm value.
     *
     * @param anAlgorithm the algorithm to set.
     */
    public void setAlgorithm(final String anAlgorithm)
    {
        this.algorithm = anAlgorithm;
    }

    /**
     * Get the algorithm vale.
     *
     * @return the algorithm
     */
    public String getAlgorithm()
    {
        return this.algorithm;
    }

    /**
     * Set the digest value.
     *
     * @param aValue the value to use.
     */
    public void setContent(byte[] aValue)
    {
        this.content = aValue;
    }

    /**
     * Get the value.
     *
     * @return the value of the digest.
     */
    public byte[] getContent()
    {
        return this.content;
    }

    /**
     * Convert the value to hex.
     *
     * @return a hexadecimal representation of the digest value.
     */
    public String toHex()
    {
        return StringOperations.hexify(content);
    }

    /**
     * Output the algorithm and value as one whole.
     *
     * @return a string.
     */
    @JsonValue
    public String toString()
    {
        return this.toString(':');
    }

    /**
     * Output the algorithm and value as one whole, separated by joinChar.
     *
     * @param aJoinChar the character to join algorithm and value.
     * @return a string.
     */
    public String toString(char aJoinChar)
    {
        return this.algorithm + aJoinChar + StringOperations.hexify(content);
    }

    /**
     * Get a pretty representation of the algorithm and the value of the digest.
     *
     * @param theJoinChar the character to join algorithms and value.
     * @return a string.
     */
    public String prettyPrint(char theJoinChar)
    {
        return this.algorithm + theJoinChar + StringOperations.split(StringOperations.hexify(content), HEX_GROUP_SIZE);
    }

    /**
     * Get a pretty representation of the algorithm and the value of the digest.
     *
     * @param theJoinChar the character to join algorithms and value.
     * @return a string.
     */
    public String prettyPrint(String theJoinChar)
    {
        return this.algorithm + theJoinChar + StringOperations.split(StringOperations.hexify(content), HEX_GROUP_SIZE);
    }

    /**
     * Implement equals.
     *
     * @param theOther the other value to compare to.
     * @return true if theOther and this have the same algorithm and the same value.
     */
    @Override
    public boolean equals(Object theOther)
    {
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
     * Compare this to the other.
     *
     * @param anOther the compare to.
     * @return an int.
     */
    @Override
    public int compareTo(Digest anOther)
    {
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
