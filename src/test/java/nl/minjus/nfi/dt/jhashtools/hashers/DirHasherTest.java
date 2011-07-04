/*
 * Copyright (c) 2010 Erwin van Eijk <erwin.vaneijk@gmail.com>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of <copyright holder>.
 */

package nl.minjus.nfi.dt.jhashtools.hashers;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;
import org.junit.*;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 *
 * @author kojak
 */
public class DirHasherTest {

    private DirHasherResult knownDigests;

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
        knownDigests = KnownDigests.getKnownResults();
    }                  

    @After
    public void tearDown() {
    }

    @Test
    public void testGetDigests() {
        try {
            DirectoryHasher directoryHasher = new SerialDirectoryHasher("sha-256");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
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
            DirectoryHasher directoryHasher = new SerialDirectoryHasher("sha-256");
            DirHasherResult digests = new DirHasherResult();
            directoryHasher.updateDigests(digests, new File("testdata"));
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
            DirectoryHasher directoryHasher = new SerialDirectoryHasher("sha-256");
            DirHasherResult digests = new DirHasherResult();
            directoryHasher.updateDigests(digests, new File("does-not-exist-testdata"));
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
            DirectoryHasher directoryHasher = new SerialDirectoryHasher("sha-256");
            assertEquals("initially no verbose behaviour", false, directoryHasher.isVerbose());
            directoryHasher.setVerbose(true);
            assertTrue(directoryHasher.isVerbose());
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetDigestsOtherWayAround() {
        try {
            DirectoryHasher directoryHasher = new SerialDirectoryHasher("sha-256");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
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
            DirectoryHasher directoryHasher = new SerialDirectoryHasher("sha-256");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
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
            DirectoryHasher directoryHasher = new SerialDirectoryHasher("md5");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
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
            DirectoryHasher directoryHasher = new SerialDirectoryHasher("sha-1");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
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
            DirectoryHasher directoryHasher = new SerialDirectoryHasher("md5");
            directoryHasher.addAlgorithm("sha-256");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            assertEquals(digests, digests.intersect(knownDigests));
            DirHasherResult knownDigestMd5 = knownDigests.getByAlgorithm("md5");
            DirHasherResult intersected = digests.intersect(knownDigestMd5);
            assertEquals(knownDigestMd5, intersected);

            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            intersected = digests.intersect(knownDigestSha256);
            assertEquals(knownDigestSha256, intersected);
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetOneOutOfThreeDigests() {
        try {
            DirectoryHasher directoryHasher = new SerialDirectoryHasher("sha-256");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            assertEquals(digests, digests.intersect(knownDigests));
            DirHasherResult knownDigestMd5 = knownDigests.getByAlgorithm("md5");
            assertEquals(13, knownDigestMd5.size());

            DirHasherResult intersected = digests.intersect(knownDigestMd5);
            assertEquals(0, intersected.size());

            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            intersected = digests.intersect(knownDigestSha256);
            assertEquals(knownDigestSha256, intersected);
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetThreeOutOfThreeDigests() {
        try {
            DirectoryHasher directoryHasher = new SerialDirectoryHasher("sha-256");
            directoryHasher.addAlgorithm("md5");
            directoryHasher.addAlgorithm("sha-1");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            assertEquals(digests, digests.intersect(knownDigests));
        }
        catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void testGetDirectoryDigestRaisedIllegalArgument() {
        try {
            DirectoryHasher directoryHasher = new SerialDirectoryHasher("sha-256");
            DirHasherResult digests = directoryHasher.getDigests(new File("does-not-exist"));
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
