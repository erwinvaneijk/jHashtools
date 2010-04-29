/*
 * Copyright (c) 2010. Erwin van Eijk <erwin.vaneijk@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools;

import nl.minjus.nfi.dt.jhashtools.utils.StringOperations;

import java.util.Arrays;

/**
 *
 * @author kojak
 */
public class Digest implements Comparable<Digest> {

    private byte[] content;
    private String algorithm;

    public Digest(String algorithm, byte[] value) {
        this.content = value;
        this.algorithm = algorithm;
    }

    public Digest(String algorithm, String hexValue) {
        this.algorithm = algorithm;
        this.content = StringOperations.hexToBytes(hexValue);
    }

    public void setAlgorithm(final String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public void setContent(byte[] value) {
        this.content = value;
    }

    public byte[] getContent() {
        return this.content;
    }

    public String toHex() {
        return StringOperations.hexify(content);
    }

    @Override
    public String toString() {
        return this.toString(':');
    }

    public String toString(char joinChar) {
        return this.algorithm + joinChar + StringOperations.hexify(content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if ((o != null) && (o instanceof Digest)) {
            Digest digest = (Digest) o;
            return (this.algorithm.equals(digest.algorithm) && (Arrays.equals(this.content, digest.content)));
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Digest o) {
        if (!this.algorithm.equals(o.algorithm)) {
            return this.algorithm.compareTo(o.algorithm);
        }

        for (int i = 0; i < Math.min(this.content.length, o.content.length); i++) {
            if (this.content[i] < o.content[i]) {
                return 1;
            } else if (this.content[i] > o.content[i]) {
                return -1;
            }
        }
        if (this.content.length == o.content.length) {
            return 0;
        } else {
            return (this.content.length - o.content.length);
        }
    }
}
