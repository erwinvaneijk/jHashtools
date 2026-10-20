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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;

/**
 * @author Erwin van Eijk
 */
public class JsonPersistenceProvider implements PersistenceProvider
{

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public JsonPersistenceProvider(boolean prettyPrint)
    {
        OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);
    }

    public JsonPersistenceProvider()
    {
        this(false);
    }

    static {
        SimpleModule module = new SimpleModule();
        module.addSerializer(DirHasherResult.class, new DirHasherResultSerializer());
        module.addDeserializer(DirHasherResult.class, new DirHasherResultDeserializer());
        OBJECT_MAPPER.registerModule(module);
    }

    @Override
    public void persist(OutputStream out, Object obj) throws PersistenceException {
        try {
            OBJECT_MAPPER.writeValue(out, obj);
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override
    public <T> T load(Reader reader, Class<T> clazz) throws PersistenceException {
        try {
            return OBJECT_MAPPER.readValue(reader, clazz);
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override
    public <T> T load(Reader reader, TypeReference<T> type) throws PersistenceException {
        try {
            return OBJECT_MAPPER.readValue(reader, type);
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }
}
