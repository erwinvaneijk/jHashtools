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

import nl.minjus.nfi.dt.jhashtools.Digest;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class OldStyleHashesTest
{

    @Test
    public void testSingleDigestValue() throws IOException, RecognitionException {
        OldStyleHashesParser parser = createParser("1111\n");
        String result = parser.digestValue();
        assertEquals("1111", result);
    }

    @Test
    public void testMultiDigestValue() throws IOException, RecognitionException {
        OldStyleHashesParser parser = createParser("1111 2222\n");
        String result = parser.digestValue();
        assertEquals("11112222", result);
    }

    @Test
    public void testMd5Digest() throws IOException, RecognitionException {
        OldStyleHashesParser parser = createParser("MD5: 1111 2222 3333 4444 5555 6666 7777 8888\n");
        Digest result = parser.digest();
        assertEquals(new Digest("md5", "11112222333344445555666677778888"), result);
    }

    @Test
    public void testMd5DigestShort() throws IOException, RecognitionException {
        OldStyleHashesParser parser = createParser("MD5: 11112222333344445555666677778888\n");
        Digest result = parser.digest();
        assertEquals(new Digest("md5", "11112222333344445555666677778888"), result);
    }

    @Test
    public void testSha1Digest() throws IOException, RecognitionException {
        OldStyleHashesParser parser = createParser("SHA-1: 0000 1111 2222 3333 4444 5555 6666 7777 8888 9999\n");
        Digest result = parser.digest();
        assertEquals(new Digest("sha-1", "0000111122223333444455556666777788889999"), result);
    }

    @Test
    public void testSha256Digest() throws IOException, RecognitionException {
        OldStyleHashesParser parser = createParser("SHA-256: 0000 1111 2222 3333 4444 5555 6666 7777 8888 9999 aaaa bbbb cccc dddd eeee ffff\n");
        Digest result = parser.digest();
        assertEquals(
            new Digest("sha-256", "0000111122223333444455556666777788889999aaaabbbbccccddddeeeeffff"), result);
    }

    private OldStyleHashesParser createParser(String testString) throws IOException {
        CharStream stream = new ANTLRStringStream(testString);
        OldStyleHashesLexer lexer = new OldStyleHashesLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        OldStyleHashesParser parser = new OldStyleHashesParser(tokens);
        return parser;
    }
}
