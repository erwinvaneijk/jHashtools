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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author eijk
 */
public class DigestTest
{

    public DigestTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
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
        assertEquals("crc", instance.getAlgorithm());
    }

    /**
     * Test of setContent method, of class Digest.
     */
    @Test
    public void testSetContent() {
        byte[] value = new byte[] { (byte) 0xde, (byte) 0xea, (byte) 0xbe, (byte) 0xef };
        Digest instance = new Digest("crc", "");
        instance.setContent(value);
        assertEquals(value, instance.getContent());
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
        assertEquals(expResult, result);
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
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Digest.
     */
    @Test
    public void testEquals() {
        Object o = new Digest("crc", "deadbeef");
        Digest instance = new Digest("crc", "deadbeef");
        assertEquals(true, instance.equals(o));
        assertEquals(true, o.equals(instance));

        assertEquals(false, instance.equals(null));
        assertEquals(false, instance.equals(new Object()));

        o = new Digest("crc", "deadbeee");
        assertEquals(false, instance.equals(o));
        assertEquals(false, o.equals(instance));

        o = new Digest("md4", "deadbeefdeadbeefdeadbeefdeadbeef");
        assertEquals(false, instance.equals(o));
        assertEquals(false, o.equals(instance));

        assertEquals(instance, instance);
    }

    /**
     * Test of compareTo method, of class Digest.
     */
    @Test
    public void testCompareTo() {
        Digest veryLess = new Digest("aaa", "deadbeee");
        Digest less = new Digest("crc", "deadbeee");
        Digest equal = new Digest("crc", "deadbeef");
        Digest more = new Digest("crc", "deadbef0");
        Digest longer = new Digest("crc", "deadbeefbeef");
        Digest instance = new Digest("crc", "deadbeef");

        assertTrue(instance.compareTo(veryLess) > 0);
        assertTrue(veryLess.compareTo(instance) < 0);
        assertEquals(0, instance.compareTo(instance));
        assertTrue(instance.compareTo(less) < 0);
        assertEquals(1, less.compareTo(instance));
        assertEquals(0, instance.compareTo(equal));
        assertEquals(1, instance.compareTo(more));
        assertTrue(instance.compareTo(longer) < 0);
    }
}