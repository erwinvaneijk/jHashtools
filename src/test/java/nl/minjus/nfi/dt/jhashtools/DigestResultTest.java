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

import nl.minjus.nfi.dt.jhashtools.exceptions.AlgorithmNotFoundException;
import nl.minjus.nfi.dt.jhashtools.exceptions.NoMatchingAlgorithmsError;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * 
 * @author Erwin van Eijk
 */
public class DigestResultTest
{

    public DigestResultTest()
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

    @Test
    public void testGetHexDigest() {
        DigestResult d = new DigestResult(new Digest("crc", "1111"));
        assertEquals("1111", d.getHexDigest("crc"));
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
        assertFalse(dr.containsResult("gpg"));
    }

    @Test
    public void testEquals() {
        DigestResult dr = new DigestResult(new Digest("crc", "deadbeef"));
        assertTrue(dr.equals(dr));
        assertFalse(dr.equals(null));
        assertFalse(dr.equals(new Object()));
    }

    @Test
    public void testMatches() {
        DigestResult dr = new DigestResult(new Digest("crc", "deadbeef"));
        dr.add(new Digest("md5", "deadbeefdeadbeefdeadbeefdeadbeef"));

        DigestResult o = new DigestResult(new Digest("md5", "deadbeefdeadbeefdeadbeefdeadbeef"));

        assertTrue(dr.matches(dr));
        assertFalse(dr.matches(null));

        assertTrue(dr.matches(o));
        assertTrue(o.matches(dr));

        o = new DigestResult(new Digest("md5", "deadbeefdeadbeefdeadbeefdeaddead"));
        assertTrue(!dr.matches(o));
        assertTrue(!o.matches(dr));

        o = new DigestResult(new Digest("sha-1", "deadbeefdeadbeefdeadbeefdeadbeefdeadbeef"));
        try {
            dr.matches(o);
            fail("An exception should have been thrown");
        } catch (NoMatchingAlgorithmsError e) {
            // This is correct
        }
    }
}
