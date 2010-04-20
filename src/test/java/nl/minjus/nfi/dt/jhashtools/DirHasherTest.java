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

    private final DirHasherResult knownDigests = KnownDigests.getKnownResults();

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
        DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
        assertEquals(knownDigestSha256, digests.intersect(knownDigestSha256));
        assertEquals(digests, digests.intersect(knownDigests));
    }

    @Test
    public void testGetDigestsOtherWayAround() {
        DirHasher dirHasher = new DirHasher("sha-256");
        DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
        assertEquals(knownDigestSha256, digests.intersect(knownDigestSha256));
        assertEquals(digests, digests.intersect(knownDigests));
        assertEquals(digests, knownDigests.intersect(digests));
    }

    @Test
    public void testGetDigestsExclude() {
        DirHasher dirHasher = new DirHasher("sha-256");
        DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
        assertEquals(0, digests.exclude(knownDigestSha256).size());
    }
    
    @Test
    public void testGetMd5Digests() {
        DirHasher dirHasher = new DirHasher("md5");
        DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        DirHasherResult knownDigestMd5 = knownDigests.getByAlgorithm("md5");
        assertEquals(knownDigestMd5, digests.intersect(knownDigestMd5));
        assertEquals(digests, digests.intersect(knownDigests));
    }

    @Test
    public void testGetShaDigests() {
        DirHasher dirHasher = new DirHasher("sha-1");
        DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        DirHasherResult knownDigestSha = knownDigests.getByAlgorithm("sha-1");
        assertEquals(knownDigestSha, digests.intersect(knownDigestSha));
        assertEquals(digests, digests.intersect(knownDigests));
    }

    @Test
    public void testGetTwoOutOfThreeDigests() {
        DirHasher dirHasher = new DirHasher("sha-256");
        dirHasher.addAlgorithm("md5");
        DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
        assertEquals(knownDigestSha256, digests.intersect(knownDigestSha256));
        assertEquals(digests, digests.intersect(knownDigests));
    }

    @Test
    public void testGetThreeOutOfThreeDigests() {
        DirHasher dirHasher = new DirHasher("sha-256");
        dirHasher.addAlgorithm("md5");
        dirHasher.addAlgorithm("sha-1");

        DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        assertEquals(digests, digests.intersect(knownDigests));
    }

    @Test
    public void testGetDirectoryDigestRaisedIllegalArgument() {
        try {
            DirHasher dirHasher = new DirHasher("sha-256");
            Map<String, DigestResult> digests = dirHasher.getDigests(new File("does-not-exist"));
            fail("We should not get here. An exception should have been thrown");
        } catch (IllegalArgumentException ex) {
            // everything is ok.
        }
    }
}
