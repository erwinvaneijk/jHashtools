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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Test;

import nl.minjus.nfi.dt.jhashtools.Digest;

public class OldStyleHashesTest
{

    @Test
    public void testSingleDigestValue() throws IOException, RecognitionException {
        OldStyleHashesParser parser = createParser("1111\n");
        String result = parser.digestValue().digestString;
        assertThat(result).isEqualTo("1111");
    }

    @Test
    public void testMultiDigestValue() throws IOException, RecognitionException {
        OldStyleHashesParser parser = createParser("1111 2222\n");
        String result = parser.digestValue().digestString;
        assertThat(result).isEqualTo("11112222");
    }

    @Test
    public void testMd5Digest() throws IOException, RecognitionException {
        OldStyleHashesParser parser = createParser("MD5: 1111 2222 3333 4444 5555 6666 7777 8888\n");
        Digest result = parser.digest().digestResult;
        assertThat(result).isEqualTo(new Digest("md5", "11112222333344445555666677778888"));
    }

    @Test
    public void testMd5DigestShort() throws IOException, RecognitionException {
        OldStyleHashesParser parser = createParser("MD5: 11112222333344445555666677778888\n");
        Digest result = parser.digest().digestResult;
        assertThat(result).isEqualTo(new Digest("md5", "11112222333344445555666677778888"));
    }

    @Test
    public void testSha1Digest() throws IOException, RecognitionException {
        OldStyleHashesParser parser = createParser("SHA-1: 0000 1111 2222 3333 4444 5555 6666 7777 8888 9999\n");
        Digest result = parser.digest().digestResult;
        assertThat(result).isEqualTo(new Digest("sha-1", "0000111122223333444455556666777788889999"));
    }

    @Test
    public void testSha256Digest() throws IOException, RecognitionException {
        OldStyleHashesParser parser =
            createParser("SHA-256: 0000 1111 2222 3333 4444 5555 6666 7777 8888 9999 aaaa bbbb cccc dddd eeee ffff\n");
        Digest result = parser.digest().digestResult;
        assertThat(result)
            .isEqualTo(new Digest("sha-256", "0000111122223333444455556666777788889999aaaabbbbccccddddeeeeffff"));
    }

    private OldStyleHashesParser createParser(String testString) throws IOException {
        CharStream stream = CharStreams.fromString(testString);
        OldStyleHashesLexer lexer = new OldStyleHashesLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        OldStyleHashesParser parser = new OldStyleHashesParser(tokens);
        return parser;
    }
}
