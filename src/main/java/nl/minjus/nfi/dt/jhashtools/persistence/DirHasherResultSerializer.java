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

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import nl.minjus.nfi.dt.jhashtools.ConstructionInfo;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

/**
 *
 * @author Erwin van Eijk
 */
public class DirHasherResultSerializer 
        implements JsonDeserializer<DirHasherResult>,
        JsonSerializer<DirHasherResult>,
        InstanceCreator<DirHasherResult>
{
    @Override
    public JsonElement serialize(DirHasherResult src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject map = new JsonObject();
        JsonElement constructionInfo = context.serialize(src.getConstructionInfo());
        map.add("constructionInfo", constructionInfo);
        for (Map.Entry<File, DigestResult> entry: src.entrySet()) {
            JsonElement valueElement = context.serialize(entry.getValue(), DigestResult.class);
            map.add(entry.getKey().toString(), valueElement);
        }
        return map;
    }


    @Override
    public DirHasherResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        DirHasherResult dirHasherResult = new DirHasherResult();
        JsonObject object = json.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry: object.entrySet()) {
            JsonElement e = entry.getValue();
            if (entry.getKey().equals("constructionInfo")) {
                Type constructionInfoType = new TypeToken<ConstructionInfo>() {}.getType();
                ConstructionInfo info = context.deserialize(e, constructionInfoType);
                dirHasherResult.setConstructionInfo(info);
            } else {
                Type digestResultType = new TypeToken<DigestResult>() {}.getType();
                DigestResult result = context.deserialize(e, digestResultType);
                String key = entry.getKey();
                dirHasherResult.put(new File(key), result);
            }
        }
        return dirHasherResult;
    }

    @Override
    public DirHasherResult createInstance(Type type) {
        return new DirHasherResult();
    }

}
