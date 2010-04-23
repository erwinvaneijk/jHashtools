/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import nl.minjus.nfi.dt.jhashtools.exceptions.AlgorithmNotFoundException;
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
public class DigestResultTest {

    public DigestResultTest() {
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
            Digest d = dr.getDigest("gpg");
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
        assertTrue(! dr.matches(o));
        assertTrue(! o.matches(dr));
    }
}
