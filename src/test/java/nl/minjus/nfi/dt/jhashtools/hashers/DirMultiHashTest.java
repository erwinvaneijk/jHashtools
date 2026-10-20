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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools.hashers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;

/**
 * @author Erwin van Eijk <erwin.vaneijk@gmail.com>
 */
public class DirMultiHashTest
{

    private final DirHasherResult knownDigests;

    public DirMultiHashTest()
    {
        this.knownDigests = KnownDigests.getKnownResults();
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testGetDigests() {
        final DirectoryHasher directoryHasher = new SerialDirectoryHasher();
        try {
            directoryHasher.addAlgorithm("sha-256");
            directoryHasher.addAlgorithm("sha-1");
            directoryHasher.addAlgorithm("md5");
            final DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(13);
            assertThat(13).isEqualTo(knownDigests.size());
            assertThat(digests.size()).isEqualTo(knownDigests.getByAlgorithm("sha-256").size());
            assertThat(digests.size()).isEqualTo(knownDigests.getByAlgorithm("sha-1").size());
            assertThat(digests.size()).isEqualTo(knownDigests.getByAlgorithm("md5").size());
            assertThat(digests.size()).isEqualTo(digests.getByAlgorithm("md5").size());
            assertThat(digests.size()).isEqualTo(digests.getByAlgorithm("sha-256").size());
            assertThat(digests.size()).isEqualTo(digests.getByAlgorithm("sha-1").size());

            for (final Map.Entry<File, DigestResult> entry : knownDigests) {
                final File filename = entry.getKey();
                final DigestResult knownResults = entry.getValue();
                assertThat(digests.containsKey(filename)).isTrue();
                final DigestResult foundResults = digests.get(filename);
                for (final Digest digest : knownResults) {
                    assertThat(foundResults.contains(digest)).as(filename + "--" + digest.toString()).isTrue();
                }
            }
        } catch (final NoSuchAlgorithmException e) {
            fail("We should not have this");
        }
    }
}
