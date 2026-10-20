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

package nl.minjus.nfi.dt.jhashtools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.minjus.nfi.dt.jhashtools.exceptions.AlgorithmNotFoundException;
import nl.minjus.nfi.dt.jhashtools.exceptions.NoMatchingAlgorithmsError;

/**
 * 
 * @author Erwin van Eijk
 */
public class DigestResultTest
{

    public DigestResultTest()
    {
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
    public void testGetHexDigest() {
        DigestResult d = new DigestResult(new Digest("crc", "1111"));
        assertThat(d.getHexDigest("crc")).isEqualTo("1111");
    }

    @Test
    public void testGetDigest() {
        DigestResult dr = new DigestResult();
        try {
            dr.getDigest("gpg");
            fail("Should have thrown AlgorithmNotFound");
        } catch (AlgorithmNotFoundException ex) {
            // pass
        }
    }

    @Test
    public void testEmptyDigestResult() {
        DigestResult dr = new DigestResult();
        assertThat(dr.containsResult("gpg")).isFalse();
    }

    @Test
    public void testEquals() {
        DigestResult dr = new DigestResult(new Digest("crc", "deadbeef"));
        assertThat(dr.equals(dr)).isTrue();
        assertThat(dr.equals(null)).isFalse();
        assertThat(dr.equals(new Object())).isFalse();
    }

    @Test
    public void testMatches() {
        DigestResult dr = new DigestResult(new Digest("crc", "deadbeef"));
        dr.add(new Digest("md5", "deadbeefdeadbeefdeadbeefdeadbeef"));

        DigestResult o = new DigestResult(new Digest("md5", "deadbeefdeadbeefdeadbeefdeadbeef"));

        assertThat(dr.matches(dr)).isTrue();
        assertThat(dr.matches(null)).isFalse();

        assertThat(dr.matches(o)).isTrue();
        assertThat(o.matches(dr)).isTrue();

        o = new DigestResult(new Digest("md5", "deadbeefdeadbeefdeadbeefdeaddead"));
        assertThat(!dr.matches(o)).isTrue();
        assertThat(!o.matches(dr)).isTrue();

        o = new DigestResult(new Digest("sha-1", "deadbeefdeadbeefdeadbeefdeadbeefdeadbeef"));
        try {
            dr.matches(o);
            fail("An exception should have been thrown");
        } catch (NoMatchingAlgorithmsError e) {
            // This is correct
        }
    }

    @Test
    public void testMatchesDetectsDifferencesAfterRepeatedUseOfAnUnregisteredAlgorithm() {
        // Issue #30: every lookup of an algorithm that is not registered up front, such as md2 in older digest
        // files, used to allocate a new mask bit until the masks overflowed and matches() compared nothing.
        for (int i = 0; i < 20; i++) {
            final DigestResult left = new DigestResult();
            left.add(new Digest("md2", "01".repeat(16)));
            left.add(new Digest("sha-256", "23".repeat(32)));
            final DigestResult right = new DigestResult();
            right.add(new Digest("md2", "45".repeat(16)));
            right.add(new Digest("sha-256", "67".repeat(32)));

            assertThat(left.matches(right)).isFalse();
        }
    }
}
