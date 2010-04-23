/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        try {
            DirHasher dirHasher = new DirHasher("sha-256");
            DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertEquals(knownDigestSha256, digests.intersect(knownDigestSha256));
            assertEquals(digests, digests.intersect(knownDigests));
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testUpdateDigests() {
        try {
            DirHasher dirHasher = new DirHasher("sha-256");
            DirHasherResult digests = new DirHasherResult();
            dirHasher.updateDigests(digests, new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertEquals(knownDigestSha256, digests.intersect(knownDigestSha256));
            assertEquals(digests, digests.intersect(knownDigests));
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testUpdateDigestsUnknownDirectory() {
        try {
            DirHasher dirHasher = new DirHasher("sha-256");
            DirHasherResult digests = new DirHasherResult();
            dirHasher.updateDigests(digests, new File("does-not-exist-testdata"));
            fail("An IllegalArgumentException should have been thrown");
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
        catch (IllegalArgumentException ex) {
            // this is ok!
        }
    }

    @Test
    public void testVerboseSettings() {
        try {
            DirHasher dirHasher = new DirHasher("sha-256");
            assertEquals("initially no verbose behaviour", false, dirHasher.isVerbose());
            dirHasher.setVerbose(true);
            assertTrue(dirHasher.isVerbose());
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetDigestsOtherWayAround() {
        try {
            DirHasher dirHasher = new DirHasher("sha-256");
            DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertEquals(knownDigestSha256, digests.intersect(knownDigestSha256));
            assertEquals(digests, digests.intersect(knownDigests));
            assertEquals(digests, knownDigests.intersect(digests));
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetDigestsExclude() {
        try {
            DirHasher dirHasher = new DirHasher("sha-256");
            DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertEquals(0, digests.exclude(knownDigestSha256).size());
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetMd5Digests() {
        try {
            DirHasher dirHasher = new DirHasher("md5");
            DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            DirHasherResult knownDigestMd5 = knownDigests.getByAlgorithm("md5");
            assertEquals(knownDigestMd5, digests.intersect(knownDigestMd5));
            assertEquals(digests, digests.intersect(knownDigests));
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetShaDigests() {
        try {
            DirHasher dirHasher = new DirHasher("sha-1");
            DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            DirHasherResult knownDigestSha = knownDigests.getByAlgorithm("sha-1");
            assertEquals(knownDigestSha, digests.intersect(knownDigestSha));
            assertEquals(digests, digests.intersect(knownDigests));
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetTwoOutOfThreeDigests() {
        try {
            DirHasher dirHasher = new DirHasher("sha-256");
            dirHasher.addAlgorithm("md5");
            DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertEquals(knownDigestSha256, digests.intersect(knownDigestSha256));
            assertEquals(digests, digests.intersect(knownDigests));
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetThreeOutOfThreeDigests() {
        try {
            DirHasher dirHasher = new DirHasher("sha-256");
            dirHasher.addAlgorithm("md5");
            dirHasher.addAlgorithm("sha-1");
            DirHasherResult digests = dirHasher.getDigests(new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            assertEquals(digests, digests.intersect(knownDigests));
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetDirectoryDigestRaisedIllegalArgument() {
        try {
            DirHasher dirHasher = new DirHasher("sha-256");
            Map<String, DigestResult> digests = dirHasher.getDigests(new File("does-not-exist"));
            fail("We should not get here. An exception should have been thrown");
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
        catch (IllegalArgumentException ex) {
            // everything is ok.
        }
    }
}
