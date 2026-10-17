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