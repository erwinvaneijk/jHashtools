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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;

/**
 *
 * @author Erwin van Eijk
 */
public class FileHasherTest
{

    private final Map<Integer, String> expectedDigests;

    private File testFile;
    private static final String DEFAULT_ALGORITHM = "sha-256";

    public FileHasherTest()
    {
        final DirHasherResult result = KnownDigests.getKnownResults();
        expectedDigests = new TreeMap<Integer, String>();
        int i = 1;
        for (final Map.Entry<File, DigestResult> entry : result) {
            if (entry.getKey().toString().contains("oldformat")) {
                continue;
            }
            if (entry.getKey().toString().contains("include")) {
                continue;
            }

            expectedDigests.put(i, entry.getValue().getHexDigest(DEFAULT_ALGORITHM));
            i += 1;
        }
    }

    @BeforeEach
    public void setUp() {
        this.testFile = new File("testdata/testfile1.bin");
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getDigest method, of class AbstractFileHasher.
     */
    @Test
    public void testGetDigest() {
        try {
            final FileHasher instance = new SerialFileHasher(DigestAlgorithmFactory.create("sha-256"));
            final String expResult = expectedDigests.get(1);
            final DigestResult results = instance.getDigest(this.testFile);
            assertThat(results.size()).isEqualTo(1);
            assertThat(results.getDigest(DEFAULT_ALGORITHM)).isNotNull();
            final String digest = results.getHexDigest(DEFAULT_ALGORITHM);
            assertThat(digest).as("Digests are not the same").isEqualTo(expResult);
        } catch (final FileNotFoundException ex) {
            fail(ex.toString());
        } catch (final IOException ex) {
            LoggerFactory.getLogger(FileHasherTest.class).error(null, ex);
            fail(ex.toString());
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString());
        }
    }

    /**
     * Test of getDigest method, of class AbstractFileHasher.
     */
    @Test
    public void testGetDigestMultithreaded() {
        try {
            final FileHasher instance = new ConcurrentFileHasher(DigestAlgorithmFactory.create("sha-256"));
            final String expResult = expectedDigests.get(1);
            final DigestResult results = instance.getDigest(this.testFile);
            assertThat(results.size()).isEqualTo(1);
            assertThat(results.getDigest(DEFAULT_ALGORITHM)).isNotNull();
            final String digest = results.getHexDigest(DEFAULT_ALGORITHM);
            assertThat(digest).as("Digests are not the same").isEqualTo(expResult);
        } catch (final FileNotFoundException ex) {
            fail(ex.toString());
        } catch (final IOException ex) {
            LoggerFactory.getLogger(FileHasherTest.class).error(null, ex);
            fail(ex.toString());
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString());
        }
    }

    @Test
    public void testComputeDigest() {
        try {
            final String expResult = expectedDigests.get(1);
            final DigestResult results = FileHasherCreator.computeDigest(this.testFile);
            final String result = results.digest().toHex();
            assertThat(result).isEqualTo(expResult);
        } catch (final FileNotFoundException ex) {
            fail(ex.toString());
        } catch (final IOException ex) {
            fail(ex.toString());
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString());
        }
    }

    @Test
    public void testFileHasherUnknownFile() {
        try {
            final FileHasher h = new SerialFileHasher(DigestAlgorithmFactory.create("md5"));
            h.getDigest(new File("Does not exist"));
            fail("Should have thrown FileNotFoundException");
        } catch (final FileNotFoundException ex) {
            // pass
        } catch (final IOException ex) {
            fail("Should not get IOException");
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString());
        }
    }

    @Test
    public void testFileHasherUnknownFileConcurrent() {
        try {
            final FileHasher h = new ConcurrentFileHasher(DigestAlgorithmFactory.create("md5"));
            h.getDigest(new File("Does not exist"));
            fail("Should have thrown FileNotFoundException");
        } catch (final FileNotFoundException ex) {
            // pass
        } catch (final IOException ex) {
            fail("Should not get IOException");
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString());
        }
    }
}