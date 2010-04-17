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
public class DirHasherTest {

    private final Map<Integer, String> knownDigests = KnownDigests.getKnownDigests().get("sha-256");

    public DirHasherTest() {
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
        DirHasher dirHasher = new DirHasher("sha-256");
        Map<String, DigestsResults> digests = dirHasher.getDigests(new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        for (Integer k: this.knownDigests.keySet()) {
            String filename = String.format("testdata/testfile%d.bin", k);
            assertTrue("The filename should exist in the map", digests.containsKey(filename));
            assertEquals(String.format("%s has wrong digest", filename),
                    this.knownDigests.get(k), 
                    digests.get(filename).getHexDigest(FileHasher.DEFAULT_ALGORITHM));
        }
    }

    @Test
    public void testGetDirectoryDigestRaisedIllegalArgument() {
        try {
            DirHasher dirHasher = new DirHasher("sha-256");
            Map<String, DigestsResults> digests = dirHasher.getDigests(new File("does-not-exist"));
            fail("We should not get here. An exception should have been thrown");
        } catch (IllegalArgumentException ex) {
            // everything is ok.
        }
    }
}