/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools.persistence;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface can be used to do persistence.
 *
 * It's a rather shallow interface.
 * @author eijk
 */
public interface Persist {

    /**
     * Persist the content of <c>obj</c> to <c>out</c>.
     * 
     * @param out The stream to send the output to
     * @param obj the object to persist.
     */
    public void persist(OutputStream out, Object obj) throws PersistenceException;

    /**
     * UnPersist the content of <c>obj</c> from <c>stream</c>.
     * 
     * @param stream
     * @param clazz
     * @return
     */
    public Object load(InputStream stream, Class clazz);
}
