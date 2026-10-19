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

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import nl.minjus.nfi.dt.jhashtools.AccessTimes;
import nl.minjus.nfi.dt.jhashtools.ConstructionInfo;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 * Arrange the deserialization of the JSON structure for DirHasherResult.
 *
 * @author Erwin van Eijk
 */
class DirHasherResultDeserializer extends JsonDeserializer<DirHasherResult>
{
    /**
     * DeSerialize a DirHasherResult.
     *
     * @param aJsonParser
     *            the JsonParser to use.
     * @param aDeserializationContext
     *            the current context.
     *
     * @return the prepared DirHasherResult.
     *
     * @throws IOException
     *             when the requested IO cannot be performed.
     */
    @SuppressWarnings("unchecked")
    @Override
    public DirHasherResult deserialize(final JsonParser aJsonParser,
        final DeserializationContext aDeserializationContext) throws IOException
    {
        final DirHasherResult result = new DirHasherResult();
        while (aJsonParser.nextToken() != JsonToken.END_OBJECT) {
            final String fieldName = aJsonParser.currentName();
            if ("constructionInfo".equals(fieldName)) {
                aJsonParser.nextToken();
                result.setConstructionInfo(aJsonParser.readValueAs(ConstructionInfo.class));
            }
            if ("content".equals(fieldName)) {
                aJsonParser.nextToken();
                while (aJsonParser.nextToken() != JsonToken.END_OBJECT) {
                    final String filePath = aJsonParser.currentName();
                    aJsonParser.nextToken();
                    final DigestResult digestResult = aJsonParser.readValueAs(DigestResult.class);
                    result.put(new File(filePath), digestResult);
                }
            }
            if ("accessTimes".equals(fieldName)) {
                aJsonParser.nextToken();
                readAccessTimes(aJsonParser, result);
            }
        }
        return result;
    }

    private static void readAccessTimes(final JsonParser aJsonParser, final DirHasherResult aResult)
        throws IOException
    {
        final JsonNode accessTimes = aJsonParser.readValueAsTree();
        if (accessTimes == null || !accessTimes.isObject()) {
            throw JsonMappingException.from(aJsonParser, "accessTimes must be an object");
        }
        for (final Map.Entry<String, JsonNode> entry : accessTimes.properties()) {
            final JsonNode times = entry.getValue();
            aResult.putAccessTimes(new File(entry.getKey()), new AccessTimes(
                readFileTime(aJsonParser, entry.getKey(), times.get("before")),
                readFileTime(aJsonParser, entry.getKey(), times.get("after"))));
        }
    }

    private static FileTime readFileTime(final JsonParser aJsonParser, final String aFilePath, final JsonNode aTime)
        throws JsonMappingException
    {
        if (aTime == null || !aTime.isTextual()) {
            throw JsonMappingException.from(aJsonParser, "Missing access time for " + aFilePath);
        }
        try {
            return FileTime.from(Instant.parse(aTime.asText()));
        } catch (final DateTimeParseException ex) {
            throw JsonMappingException.from(aJsonParser, "Invalid access time for " + aFilePath, ex);
        }
    }
}
