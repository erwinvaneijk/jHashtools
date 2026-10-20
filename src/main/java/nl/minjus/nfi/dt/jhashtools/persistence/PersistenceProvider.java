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

package nl.minjus.nfi.dt.jhashtools.persistence;

import java.io.OutputStream;
import java.io.Reader;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;

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
     * Persist the content of <code>obj</code> to <code>out</code>.
     *
     * @param out
     *            The stream to send the output to
     * @param obj
     *            the object to persist.
     *
     * @throws PersistenceException
     *             thrown when a problem occurs when persisting the data.
     */
    void persist(OutputStream out, Object obj) throws PersistenceException;

    /**
     * UnPersist the content of <code>obj</code> from <code>reader</code>.
     *
     * @param reader
     *            A Reader to read the content from.
     * @param clazz
     *            The class to finally deliver.
     *
     * @return an instance of clazz or null.
     *
     * @throws PeristenceException
     *             when an error occurrs with parsing the reader.
     */
    <T> T load(Reader reader, Class<T> clazz) throws PersistenceException;

    /**
     * UnPersist the content of <code>obj</code> from <code>reader</code>.
     *
     * @param reader
     *            A Reader to read the content from.
     * @param type
     *            The type of the class to finally deliver.
     *
     * @return an instance of clazz or null.
     *
     * @throws PeristenceException
     *             when an error occurrs with parsing the reader.
     */
    <T> T load(Reader reader, TypeReference<T> type) throws PersistenceException;
}
