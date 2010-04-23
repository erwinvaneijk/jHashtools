/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.security.NoSuchAlgorithmException;
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
public class DirVisitorTest {

    public DirVisitorTest() {
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
     * Test of visit method, of class DirVisitor.
     */
    @Test
    public void testVisit() {
        File theFile = new File("testdata/testfile1.bin");
        DirVisitor instance = new DirVisitor();
        assertTrue( ! instance.isVerbose());
        instance.visit(theFile);
        assertEquals(1, instance.getResults().size());
    }

    /**
     * Test of visit method, of class DirVisitor.
     */
    @Test
    public void testVisitNonExistant() {
        try {
            File theFile = new File("does-not-exist");
            DirVisitor instance = new DirVisitor("sha-256");
            instance.visit(theFile);
            assertEquals(0, instance.getResults().size());
        } catch (Throwable t) {
            fail("Nothing should be thrown here");
        }
    }

    /**
     * Test of visit method, of class DirVisitor.
     */
    @Test
    public void testVisitNonExistantAlgorithm() {
        try {
            DirVisitor instance = new DirVisitor("sha-345");
            fail("We should have an NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException ex) {
            // pass
        }
    }

    /**
     * Test of setVerbose method, of class DirVisitor.
     */
    @Test
    public void testSetVerbose() {
        boolean verbose = true;
        DirVisitor instance = new DirVisitor();
        instance.setVerbose(verbose);
        assertTrue(instance.isVerbose());
    }

}