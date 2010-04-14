/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools.utils;

/**
 *
 * @author kojak
 */
public class StringOperations {

    public static String hexify(final byte[] buf) {
        StringBuilder builder = new StringBuilder(buf.length * 2);
        for (byte b: buf) {
            String s = String.format("%02x", b);
            builder.append(s);
        }
        return builder.toString();
    }
}
