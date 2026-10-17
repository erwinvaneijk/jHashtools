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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

import java.io.*;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;

public class OldStylePersistenceProviderTest
{

    @Test
    public void testLoadFile() {
        try {
            File file = new File("testdata/oldformat.txt");
            Reader input = new FileReader(file, StandardCharsets.UTF_8);
            OldStylePersistenceProvider provider = new OldStylePersistenceProvider();
            DirHasherResult result = provider.load(input, DirHasherResult.class);
            assert result != null;
            assertThat(result.size()).isEqualTo(4);
            assertThat(result.get(new File("hashtree256.py"))
                .getDigest("md5").toString()).isEqualTo("md5:a4850cd827a34a7e54dacf6814e06f55");
            assertThat(result.get(new File("hashtree256.py")).getDigest("sha-1").toString())
                .isEqualTo("sha-1:23e7ace892b507b07e4dfcf1f028ee3130bc682e");

            assertThat(result.get(new File("shatest.py"))
                .getDigest("md5").toString()).isEqualTo("md5:44af6da725a24c2d8363a42069ee110f");
            assertThat(result
                .get(new File("shatest.py")).getDigest("sha-256").toString())
                .isEqualTo("sha-256:b7e94899a85df9809030e8ede16b857e90d886279dc1d3d14562142c9303dc39");

        } catch (IOException ex) {
            fail("We got an IOException. Wrong.");
        } catch (PersistenceException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testRoundTripKeepsEveryAlgorithmTheWriterEmits() throws PersistenceException {
        final DigestResult digests = new DigestResult();
        digests.add(new Digest("md5", "23".repeat(16)));
        digests.add(new Digest("sha-1", "45".repeat(20)));
        digests.add(new Digest("sha-256", "67".repeat(32)));
        digests.add(new Digest("sha-384", "89".repeat(48)));
        digests.add(new Digest("sha-512", "ab".repeat(64)));
        digests.add(new Digest("ripemd160", "cd".repeat(20)));
        final DirHasherResult written = new DirHasherResult();
        written.put("some/file.txt", digests);
        final OldStylePersistenceProvider provider = new OldStylePersistenceProvider();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        provider.persist(out, written);
        final DirHasherResult loaded =
            provider.load(new StringReader(out.toString(StandardCharsets.UTF_8)), DirHasherResult.class);

        assertThat(loaded.size()).isEqualTo(1);
        assertThat(loaded.get(new File("some/file.txt"))).isEqualTo(digests);
    }

    @Test
    public void testLegacyAlgorithmLabelsAreRecognised() throws PersistenceException {
        final String content = "Generated with: test\n"
            + "file.txt\n"
            + "\tMD5:\t" + "23".repeat(16) + "\n"
            + "\tSHA:\t" + "45".repeat(20) + "\n"
            + "\tSHA256:\t" + "67".repeat(32) + "\n";
        final OldStylePersistenceProvider provider = new OldStylePersistenceProvider();

        final DirHasherResult loaded = provider.load(new StringReader(content), DirHasherResult.class);

        final DigestResult digests = loaded.get(new File("file.txt"));
        assertThat(digests.getDigest("md5")).isEqualTo(new Digest("md5", "23".repeat(16)));
        assertThat(digests.getDigest("sha-1")).isEqualTo(new Digest("sha-1", "45".repeat(20)));
        assertThat(digests.getDigest("sha-256")).isEqualTo(new Digest("sha-256", "67".repeat(32)));
    }

    @Test
    public void testMalformedDigestLineIsRejectedWithItsLineNumber() {
        final String content = """
            Generated with: test
            file.txt
            \tthis is not a digest
            """;
        final OldStylePersistenceProvider provider = new OldStylePersistenceProvider();

        assertThatThrownBy(() -> provider.load(new StringReader(content), DirHasherResult.class))
            .isInstanceOf(PersistenceException.class)
            .hasMessageContaining("line 3");
    }

    @Test
    public void testInvalidHexDigitsAreRejected() {
        final String content = """
            Generated with: test
            file.txt
            \tmd5:\t0123 zzzz
            """;
        final OldStylePersistenceProvider provider = new OldStylePersistenceProvider();

        assertThatThrownBy(() -> provider.load(new StringReader(content), DirHasherResult.class))
            .isInstanceOf(PersistenceException.class)
            .hasMessageContaining("line 3");
    }
}
