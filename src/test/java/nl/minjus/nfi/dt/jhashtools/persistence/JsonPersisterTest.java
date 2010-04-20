/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools.persistence;

import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
     * Test of persist method, of class JsonPersister.
     */
    @Test
    public void testPersist() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DigestResult obj = new DigestResult();
            Digest d = new Digest("sha-1", "0000111122223333444455556666777788889999aaaa");
            obj.add(d);
            JsonPersister instance = new JsonPersister();
            instance.persist(out, obj);
            String str = out.toString();
            assertEquals(this.testDigestResultInJson, str);
        } catch (PersistenceException ex) {
            fail("This should not raise an exception");
        }
    }

    /**
     * Test of load method, of class JsonPersister.
     */
    @Test
    public void testLoad() {
        InputStream stream = new ByteArrayInputStream(this.testDigestResultInJson.getBytes());
        Class<DigestResult> clazz = DigestResult.class;
        JsonPersister instance = new JsonPersister();
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

            JsonPersister instance = new JsonPersister();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            instance.persist(out, obj);
            String str = out.toString();

            assertEquals(this.testDirHasherResultInJson, str);
        } catch (PersistenceException ex) {
            fail("Persistence not good.");
        }
    }

    @Test
    public void testLoadDirHasherResult() {
        InputStream stream = new ByteArrayInputStream(this.testDirHasherResultInJson.getBytes());
        Class<DirHasherResult> clazz = DirHasherResult.class;
        JsonPersister instance = new JsonPersister();
        DirHasherResult result = (DirHasherResult) instance.load(stream, clazz);
        assertEquals(1, result.size());
        assertTrue("Should contain myfile", result.containsKey("myfile"));
        DigestResult digestResult = result.get("myfile");
        assertEquals("sha-1", digestResult.digest().getAlgorithm());
        assertEquals("0000111122223333444455556666777788889999aaaa", digestResult.digest().toHex());
    }
}
