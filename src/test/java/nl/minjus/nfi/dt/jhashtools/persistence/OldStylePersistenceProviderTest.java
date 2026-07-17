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

import com.fasterxml.jackson.core.type.TypeReference;
import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class OldStylePersistenceProviderTest
{

    @Test
    public void testLoadFile() {
        try {
            File file = new File("testdata/oldformat.txt");
            Reader input = new FileReader(file);
            OldStylePersistenceProvider provider = new OldStylePersistenceProvider();
            DirHasherResult result = (DirHasherResult) provider.load(input, DirHasherResult.class);
            assert result != null;
            assertEquals(4, result.size());
            assertEquals("md5:a4850cd827a34a7e54dacf6814e06f55", result.get(new File("hashtree256.py"))
                .getDigest("md5").toString());
            assertEquals("sha-1:23e7ace892b507b07e4dfcf1f028ee3130bc682e",
                result.get(new File("hashtree256.py")).getDigest("sha-1").toString());

            assertEquals("md5:44af6da725a24c2d8363a42069ee110f", result.get(new File("shatest.py"))
                .getDigest("md5").toString());
            assertEquals("sha-256:b7e94899a85df9809030e8ede16b857e90d886279dc1d3d14562142c9303dc39", result
                .get(new File("shatest.py")).getDigest("sha-256").toString());

        } catch (IOException ex) {
            fail("We got an IOException. Wrong.");
        } catch (PersistenceException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testPersistDirHasherResult() throws PersistenceException {
        DigestResult digestResult = new DigestResult();
        digestResult.add(new Digest("md5", "a4850cd827a34a7e54dacf6814e06f55"));
        DirHasherResult obj = new DirHasherResult();
        obj.put("somefile.txt", digestResult);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OldStylePersistenceProvider provider = new OldStylePersistenceProvider();
        provider.persist(out, obj);

        String output = out.toString();
        assertTrue("Output must start with header", output.startsWith("Generated with: "));
        assertTrue("Output must contain file name", output.contains("somefile.txt"));
        assertTrue("Output must contain digest algorithm", output.contains("md5"));
    }

    @Test(expected = PersistenceException.class)
    public void testPersistNullThrows() throws PersistenceException {
        new OldStylePersistenceProvider().persist(new ByteArrayOutputStream(), null);
    }

    @Test(expected = PersistenceException.class)
    public void testPersistNonDirHasherResultThrows() throws PersistenceException {
        new OldStylePersistenceProvider().persist(new ByteArrayOutputStream(), "not a DirHasherResult");
    }

    @Test(expected = PersistenceException.class)
    public void testLoadWithTypeReferenceThrows() throws PersistenceException {
        TypeReference<DirHasherResult> typeRef = new TypeReference<DirHasherResult>() { };
        new OldStylePersistenceProvider().load(new StringReader(""), typeRef);
    }

    @Test(expected = PersistenceException.class)
    public void testLoadWrongClassThrows() throws PersistenceException {
        new OldStylePersistenceProvider().load(
            new StringReader("Generated with: something\n"),
            String.class);
    }

    @Test(expected = PersistenceException.class)
    public void testLoadBadHeaderThrows() throws PersistenceException {
        new OldStylePersistenceProvider().load(
            new StringReader("WRONG HEADER\nsomefile.txt\n"),
            DirHasherResult.class);
    }

    @Test
    public void testLoadHeaderOnlyReturnsEmptyResult() throws PersistenceException {
        DirHasherResult result = new OldStylePersistenceProvider().load(
            new StringReader("Generated with: jHashtools\n"),
            DirHasherResult.class);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testLoadSingleFileWithDigests() throws PersistenceException {
        // The parser grammar requires uppercase MD5: / SHA-1: / SHA-256: tokens.
        String input = "Generated with: jHashtools 1.0\n"
            + "somefile.txt\n"
            + "\tMD5:\ta4850cd827a34a7e54dacf6814e06f55\n"
            + "\tSHA-1:\t23e7ace892b507b07e4dfcf1f028ee3130bc682e\n";

        DirHasherResult result = new OldStylePersistenceProvider().load(
            new StringReader(input), DirHasherResult.class);

        assertEquals(1, result.size());
        assertTrue("Result must contain somefile.txt", result.containsKey("somefile.txt"));
        DigestResult dr = result.get("somefile.txt");
        assertEquals("md5:a4850cd827a34a7e54dacf6814e06f55", dr.getDigest("md5").toString());
        assertEquals("sha-1:23e7ace892b507b07e4dfcf1f028ee3130bc682e", dr.getDigest("sha-1").toString());
    }

    @Test
    public void testLoadMultipleFiles() throws PersistenceException {
        String input = "Generated with: jHashtools 1.0\n"
            + "file1.txt\n"
            + "\tMD5:\taaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\n"
            + "file2.txt\n"
            + "\tSHA-1:\tbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\n";

        DirHasherResult result = new OldStylePersistenceProvider().load(
            new StringReader(input), DirHasherResult.class);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("file1.txt"));
        assertTrue(result.containsKey("file2.txt"));
    }
}
