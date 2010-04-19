/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.util.Map;
import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;
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
public class DirMultiHashTest {

    DirHasherResult knownDigests;

    public DirMultiHashTest() {
        this.knownDigests = KnownDigests.getKnownResults();
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
    public void testGetDigests() {
        DirHasher dirHasher = new DirHasher();
        dirHasher.addAlgorithm("sha-256");
        dirHasher.addAlgorithm("sha-1");
        dirHasher.addAlgorithm("md5");
        DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
        assertEquals(10, digests.size());
        assertEquals(knownDigests.size(), 10);
        assertEquals(knownDigests.getByAlgorithm("sha-256").size(), digests.size());
        assertEquals(knownDigests.getByAlgorithm("sha-1").size(), digests.size());
        assertEquals(knownDigests.getByAlgorithm("md5").size(), digests.size());
        assertEquals(digests.getByAlgorithm("md5").size(), digests.size());
        assertEquals(digests.getByAlgorithm("sha-256").size(), digests.size());
        assertEquals(digests.getByAlgorithm("sha-1").size(), digests.size());

        for (Map.Entry<String, DigestResult> entry : knownDigests.entrySet()) {
            String filename = entry.getKey();
            DigestResult knownResults = entry.getValue();
            assertTrue(digests.containsKey(filename));
            DigestResult foundResults = digests.get(filename);
            for (Digest digest : knownResults) {
                assertTrue(digest.toString(), foundResults.contains(digest));
            }
        }
    }
}
