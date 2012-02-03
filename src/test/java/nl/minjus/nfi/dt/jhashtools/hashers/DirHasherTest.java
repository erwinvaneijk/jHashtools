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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.hashers.actors.ActingDirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test the serial dirHasher.
 * 
 * @author Erwin van Eijk
 */
@RunWith(value = Parameterized.class)
public class DirHasherTest
{

    @Parameters
    public static Collection<Object[]> data() {
        Collection<Object[]> collection = new LinkedList<Object[]>();
        try {
            collection.add(new Object[] { new SerialDirectoryHasher("sha-256"),
                    "sha-256" });
            collection.add(new Object[] { new ActingDirectoryHasher("sha-256"),
                    "sha-256" });
            collection.add(new Object[] { new SerialDirectoryHasher("sha-1"),
                    "sha-1" });
            collection.add(new Object[] { new ActingDirectoryHasher("sha-1"),
                    "sha-1" });
            collection.add(new Object[] { new SerialDirectoryHasher("md5"),
                    "md5" });
            collection.add(new Object[] { new ActingDirectoryHasher("md5"),
                    "md5" });
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return collection;
    }

    private static DirHasherResult knownDigests;

    private DirectoryHasher directoryHasher;

    private String algorithm;

    public DirHasherTest(DirectoryHasher directoryHasher, String algorithm)
    {
        this.directoryHasher = directoryHasher;
        this.algorithm = algorithm;
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        knownDigests = KnownDigests.getKnownResults();
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
        final DirHasherResult digests = 
                directoryHasher.getDigests(new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        final DirHasherResult knownDigestSha256 = knownDigests
                .getByAlgorithm(this.algorithm);
        assertEquals(knownDigestSha256, digests.intersect(knownDigestSha256));
        assertEquals(digests, digests.intersect(knownDigests));
    }

    @Test
    public void testUpdateDigests() {
        final DirHasherResult digests = new DirHasherResult();
        directoryHasher.updateDigests(digests, new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        final DirHasherResult knownDigestSha256 = knownDigests
                .getByAlgorithm(algorithm);
        assertEquals(knownDigestSha256, digests.intersect(knownDigestSha256));
        assertEquals(digests, digests.intersect(knownDigests));
    }

    @Test
    public void testUpdateDigestsUnknownDirectory() {
        try {
            final DirHasherResult digests = new DirHasherResult();
            directoryHasher.updateDigests(digests, new File(
                    "does-not-exist-testdata"));
            fail("An IllegalArgumentException should have been thrown");
        } catch (final IllegalArgumentException ex) {
            // this is ok!
        }
    }

    @Test
    public void testVerboseSettings() {
        assertEquals("initially no verbose behaviour", false,
                directoryHasher.isVerbose());
        directoryHasher.setVerbose(true);
        assertTrue(directoryHasher.isVerbose());
    }

    @Test
    public void testGetDigestsOtherWayAround() {
        final DirHasherResult digests = 
                directoryHasher.getDigests(new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        final DirHasherResult knownDigestSha256 = 
                knownDigests.getByAlgorithm(algorithm);
        assertEquals(knownDigestSha256, digests.intersect(knownDigestSha256));
        assertEquals(digests, digests.intersect(knownDigests));
        assertEquals(digests, knownDigests.intersect(digests));
    }

    @Test
    public void testGetDigestsExclude() {
        final DirHasherResult digests = 
                directoryHasher.getDigests(new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        final DirHasherResult knownDigestSha256 = 
                knownDigests.getByAlgorithm(algorithm);
        assertEquals(0, digests.exclude(knownDigestSha256).size());
    }

    @Test
    public void testGetTwoOutOfThreeDigests() {
        try {
            final DirectoryHasher directoryHasher = new SerialDirectoryHasher(
                    "md5");
            directoryHasher.addAlgorithm("sha-256");
            final DirHasherResult digests = directoryHasher
                    .getDigests(new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            assertEquals(digests, digests.intersect(knownDigests));
            final DirHasherResult knownDigestMd5 = knownDigests
                    .getByAlgorithm("md5");
            DirHasherResult intersected = digests.intersect(knownDigestMd5);
            assertEquals(knownDigestMd5, intersected);

            final DirHasherResult knownDigestSha256 = knownDigests
                    .getByAlgorithm("sha-256");
            intersected = digests.intersect(knownDigestSha256);
            assertEquals(knownDigestSha256, intersected);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetOneOutOfThreeDigests() {
        try {  
        final DirectoryHasher directoryHasher = new SerialDirectoryHasher(
                "sha-256");

        final DirHasherResult digests =
                directoryHasher.getDigests(new File("testdata"));
        assertEquals(knownDigests.size(), digests.size());
        assertEquals(digests, digests.intersect(knownDigests));
        final DirHasherResult knownDigestMd5 =
                knownDigests.getByAlgorithm("md5");
        assertEquals(13, knownDigestMd5.size());

        DirHasherResult intersected = digests.intersect(knownDigestMd5);
        assertEquals(0, intersected.size());

        final DirHasherResult knownDigestSha256 = knownDigests
                .getByAlgorithm("sha-256");
        intersected = digests.intersect(knownDigestSha256);
        assertEquals(knownDigestSha256, intersected);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetThreeOutOfThreeDigests() {
        try {
            final DirectoryHasher directoryHasher = new SerialDirectoryHasher(
                    "sha-256");
            directoryHasher.addAlgorithm("md5");
            directoryHasher.addAlgorithm("sha-1");
            final DirHasherResult digests = directoryHasher
                    .getDigests(new File("testdata"));
            assertEquals(knownDigests.size(), digests.size());
            assertEquals(digests, digests.intersect(knownDigests));
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void testGetDirectoryDigestRaisedIllegalArgument() {
        try {
            final DirHasherResult digests = directoryHasher
                    .getDigests(new File("does-not-exist"));
            fail("We should not get here. An exception should have been thrown");
        } catch (final IllegalArgumentException ex) {
            // everything is ok.
        }
    }
}
