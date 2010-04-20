/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;
import org.junit.After;
import org.junit.Before;
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
        DirHasherResult result = KnownDigests.getKnownResults();
        expectedDigests = new TreeMap<Integer, String>();
        int i = 1;
        for (Map.Entry<String, DigestResult> entry: result.entrySet()) {
            expectedDigests.put(i, entry.getValue().getHexDigest(FileHasher.DEFAULT_ALGORITHM));
            i += 1;
        }
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
        try {
            FileHasher instance = new FileHasher(FileHasher.DEFAULT_ALGORITHM);
            String expResult = expectedDigests.get(1);
            DigestResult results = instance.getDigest(this.testFile);
            assertEquals(1, results.size());
            assertNotNull(results.getDigest(FileHasher.DEFAULT_ALGORITHM));
            String digest = results.getHexDigest(FileHasher.DEFAULT_ALGORITHM);
            assertEquals("Digests are not the same", expResult, digest);
        } catch (FileNotFoundException ex) {
            fail(ex.toString());
        } catch (IOException ex) {
            Logger.getLogger(FileHasherTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.toString());
        }
    }

    @Test
    public void testComputeDigest() {
        try {
            String expResult = expectedDigests.get(1);
            DigestResult results = FileHasher.computeDigest(this.testFile);
            String result = results.digest().toHex();
            assertEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            fail(ex.toString());
        } catch (IOException ex) {
            fail(ex.toString());
        }
    }
}