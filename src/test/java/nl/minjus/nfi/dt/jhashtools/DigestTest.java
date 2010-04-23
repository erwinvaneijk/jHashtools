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
public class DigestTest {

    public DigestTest() {
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
        byte[] value = new byte[] { (byte)0xde, (byte)0xea, (byte)0xbe, (byte)0xef };
        Digest instance = new Digest("crc", "");
        instance.setContent(value);
        assertEquals(value, instance.getContent());
    }

    /**
     * Test of toHex method, of class Digest.
     */
    @Test
    public void testToHex() {
        byte[] value = new byte[] { (byte)0xde, (byte)0xed, (byte)0xbe, (byte)0xef };
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
        byte[] value = new byte[] { (byte)0xde, (byte)0xed, (byte)0xbe, (byte)0xef };
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