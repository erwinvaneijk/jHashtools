/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools;

import java.util.Arrays;
import nl.minjus.nfi.dt.jhashtools.utils.StringOperations;

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
        return this.algorithm + ":" + StringOperations.hexify(content);
    }

    @Override
    public boolean equals(Object o) {
        if ((o != null) && (o instanceof Digest)) {
            Digest digest = (Digest) o;
            if (this == o) {
                return true;
            }
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
