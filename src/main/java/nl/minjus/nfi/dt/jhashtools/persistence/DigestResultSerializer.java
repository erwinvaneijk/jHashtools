/*
 * Copyright (c) 2010. Erwin van Eijk <erwin.vaneijk@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.minjus.nfi.dt.jhashtools.persistence;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;

import java.lang.reflect.Type;

/**
 *
 * @author eijk
 */
class DigestResultSerializer
        implements JsonSerializer<DigestResult>, JsonDeserializer<DigestResult>
{

    public DigestResultSerializer() {
    }

    @Override
    public JsonElement serialize(DigestResult src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray arr =  new JsonArray();
        for (Digest d : src) {
            JsonElement valueElement = context.serialize(d, d.getClass());
            arr.add(valueElement);
        }
        return arr;
    }

    @Override
    public DigestResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        DigestResult digestResult = new DigestResult();
        JsonArray array = json.getAsJsonArray();
        for (int i=0; i<array.size(); ++i) {
            JsonElement a = array.get(i);
            Type digestType = new TypeToken<Digest>() {}.getType();
            Digest digest = context.deserialize(a, digestType);
            digestResult.add(digest);
        }
        return digestResult;
    }
}
