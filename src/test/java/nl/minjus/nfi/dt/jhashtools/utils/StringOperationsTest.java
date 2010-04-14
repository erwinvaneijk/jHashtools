/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kojak
 */
public class StringOperationsTest {

    public StringOperationsTest() {
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
     * Test of hexify method, of class StringOperations.
     */
    @Test
    public void testHexify() {
        System.out.println("hexify");
        byte[] buf = new byte[] { 0x10, 0x11, 0x12, 0x13 };
        String expResult = "10111213";
        String result = StringOperations.hexify(buf);
        assertEquals(expResult, result);
    }

}