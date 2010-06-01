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

import nl.minjus.nfi.dt.jhashtools.ConstructionInfo;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Arrange the deserialization of the JSON structure for DirHasherResult.
 *
 * @author Erwin van Eijk
 */
class DirHasherResultDeserializer extends JsonDeserializer<DirHasherResult>
{
    /**
     * Deserialize a DirHasherResult.
     *
     * @param aJsonParser             the JsonParser to use.
     * @param aDeserializationContext the current context.
     *
     * @return the prepared DirHasherResult.
     *
     * @throws IOException when the requested IO cannot be performed.
     */
    @Override
    public DirHasherResult deserialize(final JsonParser aJsonParser,
                                       final DeserializationContext aDeserializationContext)
            throws IOException
    {
        final DirHasherResult result = new DirHasherResult();
        while (aJsonParser.nextToken() != JsonToken.END_OBJECT) {
            final String fieldName = aJsonParser.getCurrentName();
            if ("constructionInfo".equals(fieldName)) {
                aJsonParser.nextToken();
                result.setConstructionInfo(aJsonParser.readValueAs(ConstructionInfo.class));
            }
            if ("content".equals(fieldName)) {
                aJsonParser.nextToken();
                result.putAll(
                        (Map<File, DigestResult>) aJsonParser.readValueAs(
                                new TypeReference<Map<File, DigestResult>>()
                                {
                                }));
            }
        }
        return result;
    }
}
