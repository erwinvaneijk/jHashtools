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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools.hashers;

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;
import org.junit.*;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author kojak
 */
public class DirMultiHashTest
{

    private final DirHasherResult knownDigests;

    public DirMultiHashTest()
    {
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
        DirectoryHasher directoryHasher = new SerialDirectoryHasher();
        try {
            directoryHasher.addAlgorithm("sha-256");
            directoryHasher.addAlgorithm("sha-1");
            directoryHasher.addAlgorithm("md5");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertEquals(13, digests.size());
            assertEquals(knownDigests.size(), 13);
            assertEquals(knownDigests.getByAlgorithm("sha-256").size(), digests.size());
            assertEquals(knownDigests.getByAlgorithm("sha-1").size(), digests.size());
            assertEquals(knownDigests.getByAlgorithm("md5").size(), digests.size());
            assertEquals(digests.getByAlgorithm("md5").size(), digests.size());
            assertEquals(digests.getByAlgorithm("sha-256").size(), digests.size());
            assertEquals(digests.getByAlgorithm("sha-1").size(), digests.size());

            for (Map.Entry<File, DigestResult> entry : knownDigests) {
                File filename = entry.getKey();
                DigestResult knownResults = entry.getValue();
                assertTrue(digests.containsKey(filename));
                DigestResult foundResults = digests.get(filename);
                for (Digest digest : knownResults) {
                    assertTrue(filename + "--" + digest.toString(), foundResults.contains(digest));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            fail("We should not have this");
        }
    }
}
