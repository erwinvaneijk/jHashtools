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

package nl.minjus.nfi.dt.jhashtools.persistence;

import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import org.codehaus.jackson.type.TypeReference;

import java.io.OutputStream;
import java.io.Reader;

/**
 * This interface can be used to do persistence.
 * <p/>
 * It's a rather shallow interface.
 *
 * @author Erwin van Eijk
 */
public interface PersistenceProvider
{

    /**
     * Persist the content of <c>obj</c> to <c>out</c>.
     *
     * @param out The stream to send the output to
     * @param obj the object to persist.
     *
     * @throws PersistenceException thrown when a problem occurs when persisting the data.
     */
    public void persist(OutputStream out, Object obj) throws PersistenceException;

    /**
     * UnPersist the content of <c>obj</c> from <c>reader</c>.
     *
     * @param reader A Reader to read the content from.
     * @param clazz  The class to finally deliver.
     *
     * @return an instance of clazz or null.
     *
     * @throws PeristenceException when an error occurrs with parsing the reader.
     */
    public <T> T load(Reader reader, Class<T> clazz) throws PersistenceException;

    /**
     * UnPersist the content of <c>obj</c> from <c>reader</c>.
     *
     * @param reader A Reader to read the content from.
     * @param type   The type of the class to finally deliver.
     *
     * @return an instance of clazz or null.
     *
     * @throws PeristenceException when an error occurrs with parsing the reader.
     */
    public <T> T load(Reader reader, TypeReference<T> type) throws PersistenceException;
}
