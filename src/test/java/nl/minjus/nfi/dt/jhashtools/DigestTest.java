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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author eijk
 */
public class DigestTest
{

    public DigestTest()
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

    /**
     * Test of setAlgorithm method, of class Digest.
     */
    @Test
    public void testSetAlgorithm() {
        String algorithm = "crc";
        Digest instance = new Digest("foo", "deadbeef");
        instance.setAlgorithm(algorithm);
        assertThat(instance.getAlgorithm()).isEqualTo("crc");
    }

    /**
     * Test of setContent method, of class Digest.
     */
    @Test
    public void testSetContent() {
        byte[] value = new byte[] { (byte) 0xde, (byte) 0xea, (byte) 0xbe, (byte) 0xef };
        Digest instance = new Digest("crc", "");
        instance.setContent(value);
        assertThat(instance.getContent()).isEqualTo(value);
    }

    /**
     * Test of toHex method, of class Digest.
     */
    @Test
    public void testToHex() {
        byte[] value = new byte[] { (byte) 0xde, (byte) 0xed, (byte) 0xbe, (byte) 0xef };
        Digest instance = new Digest("crc", value);
        String expResult = "deedbeef";
        String result = instance.toHex();
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Test of toString method, of class Digest.
     */
    @Test
    public void testToString() {
        byte[] value = new byte[] { (byte) 0xde, (byte) 0xed, (byte) 0xbe, (byte) 0xef };
        Digest instance = new Digest("crc", value);
        String expResult = "crc:deedbeef";
        String result = instance.toString();
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Test of equals method, of class Digest.
     */
    @Test
    @SuppressWarnings("SelfEquals") // deliberately checks that equals is reflexive
    public void testEquals() {
        Object o = new Digest("crc", "deadbeef");
        Digest instance = new Digest("crc", "deadbeef");
        assertThat(instance.equals(o)).isEqualTo(true);
        assertThat(o.equals(instance)).isEqualTo(true);

        assertThat(instance.equals(null)).isEqualTo(false);
        assertThat(instance.equals(new Object())).isEqualTo(false);

        o = new Digest("crc", "deadbeee");
        assertThat(instance.equals(o)).isEqualTo(false);
        assertThat(o.equals(instance)).isEqualTo(false);

        o = new Digest("md4", "deadbeefdeadbeefdeadbeefdeadbeef");
        assertThat(instance.equals(o)).isEqualTo(false);
        assertThat(o.equals(instance)).isEqualTo(false);

        assertThat(instance.equals(instance)).isTrue();
    }

    /**
     * Test of compareTo method, of class Digest.
     */
    @Test
    @SuppressWarnings("SelfComparison") // deliberately checks compareTo against itself
    public void testCompareTo() {
        Digest veryLess = new Digest("aaa", "deadbeee");
        Digest less = new Digest("crc", "deadbeee");
        Digest equal = new Digest("crc", "deadbeef");
        Digest more = new Digest("crc", "deadbef0");
        Digest longer = new Digest("crc", "deadbeefbeef");
        Digest instance = new Digest("crc", "deadbeef");

        assertThat(instance.compareTo(veryLess) > 0).isTrue();
        assertThat(veryLess.compareTo(instance) < 0).isTrue();
        assertThat(instance.compareTo(instance)).isEqualTo(0);
        assertThat(instance.compareTo(less) < 0).isTrue();
        assertThat(less.compareTo(instance)).isEqualTo(1);
        assertThat(instance.compareTo(equal)).isEqualTo(0);
        assertThat(instance.compareTo(more)).isEqualTo(1);
        assertThat(instance.compareTo(longer) < 0).isTrue();
    }
}