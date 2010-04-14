/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;
import nl.minjus.nfi.dt.jhashtools.utils.StringOperations;
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
public class FileHasherTest {

    private Map<Integer, String> expectedDigests;

    private File testFile;

    public FileHasherTest() {
        expectedDigests = KnownDigests.getKnownDigests();
    }

    @Before
    public void setUp() {
        this.testFile = new File("testdata/testfile1.bin");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDigest method, of class FileHasher.
     */
    @Test
    public void testGetDigest() {
        System.out.println("getDigest");
        try {
            FileHasher instance = new FileHasher("sha-256");
            String expResult = expectedDigests.get(1);
            byte[] resultBytes = instance.getDigest(this.testFile);
            String result = StringOperations.hexify(resultBytes);
            assertEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            fail(ex.toString());
        } catch (IOException ex) {
            Logger.getLogger(FileHasherTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.toString());
        }
    }

    @Test
    public void testComputeDigest() {
        System.out.println("computeDigest");
        try {
            String expResult = expectedDigests.get(1);
            byte[] digest = FileHasher.computeDigest(this.testFile);
            String result = StringOperations.hexify(digest);
            assertEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            fail(ex.toString());
        } catch (IOException ex) {
            fail(ex.toString());
        }
    }
}