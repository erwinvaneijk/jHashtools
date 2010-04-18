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

    Map<String, Map<Integer, String>> knownDigests;

    public DirMultiHashTest() {
        this.knownDigests = KnownDigests.getKnownDigests();
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
        Map<String, DigestResult> digests = dirHasher.getDigests(new File("testdata"));
        assertEquals(10, digests.size());
        assertEquals(knownDigests.size(), 2);
        assertEquals(knownDigests.get("sha-256").size(), digests.size());
        assertEquals(knownDigests.get("sha-1").size(), digests.size());

        for (Map.Entry<String, Map<Integer, String>> entry : knownDigests.entrySet()) {
            String algorithm = entry.getKey();
            Map<Integer, String> knownAlgorithmDigests = entry.getValue();
            for (Map.Entry<Integer, String> fileDigestPair : knownAlgorithmDigests.entrySet()) {
                int k = fileDigestPair.getKey();
                String expectedDigest = fileDigestPair.getValue();
                String filename = String.format("testdata/testfile%d.bin", k);
                assertTrue("The filename should exist in the map", digests.containsKey(filename));
                assertEquals(String.format("%s has wrong %s digest", algorithm, filename),
                        expectedDigest,
                        digests.get(filename).getHexDigest(algorithm));
            }
        }
    }
}
