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

import java.io.File;
import nl.minjus.nfi.dt.jhashtools.ConstructionInfo;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;
import java.util.Map;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.type.TypeReference;

/**
 * Arrange the deserialization of the JSON structure for DirHasherResult
 */
public class DirHasherResultDeserializer extends JsonDeserializer<DirHasherResult> {

    @Override
    public DirHasherResult deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        DirHasherResult result = new DirHasherResult();
        //if (jp.nextToken() != JsonToken.START_OBJECT) {
        //    throw new JsonParseException("Did not find a START_OBJECT marker", jp.getTokenLocation());
        //}
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            if ("constructionInfo".equals(fieldName)) {
                jp.nextToken();
                result.setConstructionInfo(jp.readValueAs(ConstructionInfo.class));
            }
            if ("content".equals(fieldName)) {
                jp.nextToken();
                result.putAll((Map<File, DigestResult>) jp.readValueAs(new TypeReference<Map<File, DigestResult>>() { }));
            }
        }
        return result;
    }
}
