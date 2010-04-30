/*
 * Copyright (c) 2010. Erwin van Eijk <erwin.vaneijk@gmail.com>
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
 */

package nl.minjus.nfi.dt.jhashtools.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;

import java.io.*;
import java.lang.reflect.Type;

/**
 *
 * @author eijk
 */
public class JsonPersistenceProvider implements PersistenceProvider {

    private final GsonBuilder gsonBuilder;

    public JsonPersistenceProvider() {
        // pass
        gsonBuilder = new GsonBuilder();
        this.gsonBuilder.setPrettyPrinting();
        Type digestType=new TypeToken<Digest>() {}.getType();
        this.gsonBuilder.registerTypeAdapter(digestType, new DigestSerializer());
        Type fullDigestList=new TypeToken<DirHasherResult>() {}.getType();
        Type digestResultType=new TypeToken<DigestResult>() {}.getType();
        this.gsonBuilder.registerTypeAdapter(digestResultType, new DigestResultSerializer());
        this.gsonBuilder.registerTypeAdapter(fullDigestList, new DirHasherResultSerializer());
    }

    @Override
    public void persist(OutputStream out, Object obj) throws PersistenceException {
        try {
            Gson gson = this.gsonBuilder.create();
            String result = gson.toJson(obj);
            out.write(result.getBytes());
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Object load(InputStream stream, Class clazz) {
        Gson gson = this.gsonBuilder.create();
        Reader reader = new InputStreamReader(stream);
        return gson.fromJson(reader, clazz);
    }
}
