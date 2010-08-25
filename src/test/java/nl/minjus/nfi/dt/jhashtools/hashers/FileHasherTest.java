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

import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 *
 * @author Erwin van Eijk
 */
public class FileHasherTest {

    private final Map<Integer, String> expectedDigests;

    private File testFile;

    public FileHasherTest() {
        DirHasherResult result = KnownDigests.getKnownResults();
        expectedDigests = new TreeMap<Integer, String>();
        int i = 1;
        for (Map.Entry<File, DigestResult> entry: result) {
            if (entry.getKey().toString().contains("oldformat")) {
                continue;
            }
            if (entry.getKey().toString().contains("include")) {
                continue;
            }

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
     * Test of getDigest method, of class AbstractFileHasher.
     */
    @Test
    public void testGetDigest() {
        try {
            FileHasher instance = new SerialFileHasher("sha-256");
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
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString());
        }
    }


    /**
     * Test of getDigest method, of class AbstractFileHasher.
     */
    @Test
    public void testGetDigestMultithreaded() {
        try {
            FileHasher instance = new ConcurrentFileHasher("sha-256");
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
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString());
        }
    }

    @Test
    public void testComputeDigest() {
        try {
            String expResult = expectedDigests.get(1);
            DigestResult results = FileHasherCreator.computeDigest(this.testFile);
            String result = results.digest().toHex();
            assertEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            fail(ex.toString());
        } catch (IOException ex) {
            fail(ex.toString());
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString());
        }
    }

    @Test
    public void testFileHasherUnknownFile() {
        try {
            FileHasher h = new SerialFileHasher("md5");
            DigestResult d = h.getDigest(new File("Does not exist"));
            fail("Should have thrown FileNotFoundException");
        } catch (FileNotFoundException ex) {
            // pass
        } catch (IOException ex) {
            fail("Should not get IOException");
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString());
        }
    }

    @Test
    public void testFileHasherUnknownFileConcurrent() {
        try {
            FileHasher h = new ConcurrentFileHasher("md5");
            DigestResult d = h.getDigest(new File("Does not exist"));
            fail("Should have thrown FileNotFoundException");
        } catch (FileNotFoundException ex) {
            // pass
        } catch (IOException ex) {
            fail("Should not get IOException");
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString());
        }
    }
}