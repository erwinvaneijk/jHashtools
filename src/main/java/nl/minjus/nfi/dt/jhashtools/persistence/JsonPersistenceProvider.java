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

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.deser.CustomDeserializerFactory;
import org.codehaus.jackson.map.deser.StdDeserializerProvider;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

/**
 * @author Erwin van Eijk
 */
public class JsonPersistenceProvider implements PersistenceProvider
{

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public JsonPersistenceProvider(boolean prettyPrint)
    {
        OBJECT_MAPPER.getSerializationConfig().set(Feature.INDENT_OUTPUT, prettyPrint);
        OBJECT_MAPPER.getSerializationConfig().set(Feature.WRITE_DATES_AS_TIMESTAMPS, !prettyPrint);
    }

    public JsonPersistenceProvider()
    {
        this(false);
    }

    static {
        final CustomSerializerFactory sf = new CustomSerializerFactory();
        sf.addGenericMapping(DirHasherResult.class, new DirHasherResultSerializer());
        OBJECT_MAPPER.setSerializerFactory(sf);

        final CustomDeserializerFactory df = new CustomDeserializerFactory();
        df.addSpecificMapping(DirHasherResult.class, new DirHasherResultDeserializer());
        OBJECT_MAPPER.setDeserializerProvider(new StdDeserializerProvider(df));
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T load(Reader reader, TypeReference<T> type) throws PersistenceException {
        try {
            return (T) OBJECT_MAPPER.readValue(reader, type);
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }
}
