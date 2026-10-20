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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(DirHasherResultDeserializer.class);

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
            } else if ("content".equals(fieldName)) {
                aJsonParser.nextToken();
                while (aJsonParser.nextToken() != JsonToken.END_OBJECT) {
                    final String filePath = aJsonParser.currentName();
                    aJsonParser.nextToken();
                    final DigestResult digestResult = aJsonParser.readValueAs(DigestResult.class);
                    result.put(new File(filePath), digestResult);
                }
            } else if ("accessTimes".equals(fieldName)) {
                aJsonParser.nextToken();
                readAccessTimes(aJsonParser, result);
            } else {
                LOG.warn("Skipping unknown field '{}' in digest file", fieldName);
                aJsonParser.nextToken();
                aJsonParser.skipChildren();
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
