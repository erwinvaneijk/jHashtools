/*
 * Copyright (c) 2010. Erwin van Eijk <erwin.vaneijk@gmail.com>
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
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 *
 * @author kojak
 */
public class FileHasherTest {

    private final Map<Integer, String> expectedDigests;

    private File testFile;

    public FileHasherTest() {
        DirHasherResult result = KnownDigests.getKnownResults();
        expectedDigests = new TreeMap<Integer, String>();
        int i = 1;
        for (Map.Entry<File, DigestResult> entry: result.entrySet()) {
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

    @Test
    public void testNoSuchAlgorithmOnFileHasher() {
        try {
            FileHasher h = new FileHasher("foo");
            fail("Should have thrown NoSuchAlgorithmException");
        } catch (RuntimeException ex) {
            // pass
        }
    }

    @Test
    public void testNoSuchAlgorithmOnFileHasherCollection() {
        try {
            ArrayList<String> algorithms = new ArrayList<String>();
            algorithms.add("md5");
            algorithms.add("foo");
            FileHasher h = new FileHasher(algorithms);
            fail("Should have thrown NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException ex) {
            // pass
        }
    }

    @Test
    public void testFileHasherUnknownFile() {
        try {
            FileHasher h = new FileHasher("md5");
            DigestResult d = h.getDigest(new File("Does not exist"));
            fail("Should have thrown FileNotFoundException");
        } catch (FileNotFoundException ex) {
            // pass
        } catch (IOException ex) {
            fail("Should not get IOException");
        }
    }
}