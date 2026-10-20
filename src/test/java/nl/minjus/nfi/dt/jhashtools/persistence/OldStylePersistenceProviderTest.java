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
