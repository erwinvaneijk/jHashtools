/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
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
public class FileWalkerTest {

    public FileWalkerTest() {
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
     * Test of addWalkerVisitor method, of class FileWalker.
     */
    @Test
    public void testAddWalkerVisitor() {
        WalkerVisitor visitor = null;
        FileWalker instance = new FileWalker();
        instance.addWalkerVisitor(visitor);
        assertEquals(1, instance.getWalkerVisitors().size());
        assertEquals(null, instance.getWalkerVisitors().get(0));
    }

    /**
     * Test of walk method, of class FileWalker.
     */
    @Test
    public void testWalkWithUnknownFile() {
        File file = new File("unknown");
        FileWalker instance = new FileWalker();
        int expResult = 0;
        int result = instance.walk(file);
        assertEquals(expResult, result);
    }

    class WalkerVisitorImpl implements WalkerVisitor {
        private int number;

        public WalkerVisitorImpl() {
            this.number = 0;
        }

        public final int getNumber() {
            return this.number;
        }

        @Override
        public void visit(File file) {
            number += 1;
        };
    };

    /**
     * Test of walk method, of class FileWalker.
     */
    @Test
    public void testWalkWithKnownDirStructure() {
        File file = new File("testdata");
        FileWalker instance = new FileWalker();
        WalkerVisitorImpl visitor = new WalkerVisitorImpl();
        instance.addWalkerVisitor(visitor);
        int expResult = 10;
        int result = instance.walk(file);
        assertEquals(expResult, result);
        assertEquals(10, visitor.getNumber());
    }
}