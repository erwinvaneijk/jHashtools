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
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.minjus.nfi.dt.jhashtools.AccessTimes;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 * @author Erwin van Eijk
 */
class DirHasherResultSerializer extends JsonSerializer<DirHasherResult>
{
    @Override
    public void serialize(final DirHasherResult dirHasherResult, final JsonGenerator jsonGenerator,
        final SerializerProvider serializerProvider) throws IOException, JsonProcessingException
    {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("constructionInfo");
        jsonGenerator.writeObject(dirHasherResult.getConstructionInfo());

        jsonGenerator.writeFieldName("content");
        jsonGenerator.writeStartObject();
        for (Map.Entry<File, DigestResult> entry : dirHasherResult.getContent().entrySet()) {
            jsonGenerator.writeFieldName(entry.getKey().getPath());
            jsonGenerator.writeObject(entry.getValue());
        }
        jsonGenerator.writeEndObject();

        jsonGenerator.writeFieldName("accessTimes");
        jsonGenerator.writeStartObject();
        for (Map.Entry<File, AccessTimes> entry : dirHasherResult.getAccessTimes().entrySet()) {
            jsonGenerator.writeFieldName(entry.getKey().getPath());
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("before", entry.getValue().before().toInstant().toString());
            jsonGenerator.writeStringField("after", entry.getValue().after().toInstant().toString());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndObject();
    }
}
