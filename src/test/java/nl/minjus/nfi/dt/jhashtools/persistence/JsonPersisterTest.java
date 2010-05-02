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
package nl.minjus.nfi.dt.jhashtools.persistence;

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import org.junit.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 *
 * @author eijk
 */
public class JsonPersisterTest {

    private String testDigestResultInJson;
    private String testDirHasherResultInJson;

    public JsonPersisterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        this.testDigestResultInJson = "[[\"sha-1\",\"0000111122223333444455556666777788889999aaaa\"]]\n";
        this.testDirHasherResultInJson = "{\"myfile\":[[\"sha-1\",\"0000111122223333444455556666777788889999aaaa\"]]}\n";
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of persist method, of class JsonPersistenceProvider.
     */
    @Test
    public void testPersist() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DigestResult obj = new DigestResult();
            Digest d = new Digest("sha-1", "0000111122223333444455556666777788889999aaaa");
            obj.add(d);
            JsonPersistenceProvider instance = new JsonPersistenceProvider();
            instance.persist(out, obj);
            String str = out.toString();
            assertEquals(this.testDigestResultInJson, str);
        } catch (PersistenceException ex) {
            fail("This should not raise an exception");
        }
    }

    /**
     * Test of load method, of class JsonPersistenceProvider.
     */
    @Test
    public void testLoad() throws PersistenceException {
        InputStream stream = new ByteArrayInputStream(this.testDigestResultInJson.getBytes());
        Class<DigestResult> clazz = DigestResult.class;
        JsonPersistenceProvider instance = new JsonPersistenceProvider();
        DigestResult result = (DigestResult) instance.load(stream, clazz);
        assertEquals(1, result.size());
        assertEquals("sha-1", result.digest().getAlgorithm());
        assertEquals("0000111122223333444455556666777788889999aaaa", result.digest().toHex());
    }

    @Test
    public void testPersistDirHasherResult() {
        try {
            Digest d = new Digest("sha-1", "0000111122223333444455556666777788889999aaaa");
            DigestResult digestResult = new DigestResult();
            digestResult.add(d);
            DirHasherResult obj = new DirHasherResult();
            obj.put("myfile", digestResult);

            JsonPersistenceProvider instance = new JsonPersistenceProvider();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            instance.persist(out, obj);
            String str = out.toString();

            InputStream stream = new ByteArrayInputStream(str.getBytes());
            DirHasherResult res = (DirHasherResult) instance.load(stream, DirHasherResult.class);
            assertEquals(obj, res);
        } catch (PersistenceException ex) {
            fail("Persistence not good.");
        }
    }

    @Test
    public void testLoadDirHasherResult() throws PersistenceException {
        InputStream stream = new ByteArrayInputStream(this.testDirHasherResultInJson.getBytes());
        Class<DirHasherResult> clazz = DirHasherResult.class;
        JsonPersistenceProvider instance = new JsonPersistenceProvider();
        DirHasherResult result = (DirHasherResult) instance.load(stream, clazz);
        assertEquals(1, result.size());
        assertTrue("Should contain myfile", result.containsKey("myfile"));
        DigestResult digestResult = result.get("myfile");
        assertEquals("sha-1", digestResult.digest().getAlgorithm());
        assertEquals("0000111122223333444455556666777788889999aaaa", digestResult.digest().toHex());
    }
}
