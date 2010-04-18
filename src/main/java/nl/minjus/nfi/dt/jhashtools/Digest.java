/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import nl.minjus.nfi.dt.jhashtools.utils.StringOperations;

/**
 *
 * @author kojak
 */
public class Digest {
    private byte[] content;

    public Digest(byte[] value) {
        this.content = value;
    }

    public Digest(String value) {
        throw new UnsupportedOperationException();
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
    
    public String toString() {
        return StringOperations.hexify(content);
    }
}
