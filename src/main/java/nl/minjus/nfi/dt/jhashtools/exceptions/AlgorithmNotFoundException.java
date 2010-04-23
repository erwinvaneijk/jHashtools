/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools.exceptions;

/**
 *
 * @author eijk
 */
public class AlgorithmNotFoundException extends RuntimeException {

    public AlgorithmNotFoundException() {
    }

    public AlgorithmNotFoundException(String string) {
        super(string);
    }

}
