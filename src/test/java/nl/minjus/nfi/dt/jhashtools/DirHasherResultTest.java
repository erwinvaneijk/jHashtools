/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

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
public class DirHasherResultTest {

    public DirHasherResult setOne;
    public DirHasherResult setTwo;

    public DirHasherResultTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        setOne = new DirHasherResult();
        setOne.put("one", new DigestResult(new Digest("crc", "1111")));
        setOne.put("two", new DigestResult(new Digest("crc", "1111")));
        setOne.put("three", new DigestResult(new Digest("crc", "2222")));

        setTwo = new DirHasherResult();
        setTwo.put("one", new DigestResult(new Digest("crc", "1111")));
        setTwo.put("two", new DigestResult(new Digest("crc", "1111")));
        setTwo.put("three", new DigestResult(new Digest("crc", "2222")));
    }

    @After
    public void tearDown() {
        setOne.clear();
        setTwo.clear();
    }

    /**
     * Test of exclude method, of class DirHasherResult.
     */
    @Test
    public void testExclude() {
        DirHasherResult result = setOne.exclude(setTwo);
        assertEquals(0, result.size());
    }

    @Test
    public void testEcludeOneLeft() {
        setTwo.put("four", new DigestResult(new Digest("crc", "2222")));
        DirHasherResult result = setTwo.exclude(setOne);
        assertEquals(1, result.size());
        assertEquals(4, setTwo.size());
        assertEquals(3, setOne.size());
        assertTrue(result.containsKey("four"));
    }

    @Test
    public void testExcludeOneLeftOtherSize() {
        setOne.put("four", new DigestResult(new Digest("crc", "2222")));
        DirHasherResult result = setTwo.exclude(setOne);
        assertEquals(0, result.size());
        assertEquals(3, setTwo.size());
        assertEquals(4, setOne.size());
    }

    @Test
    public void testExcludeOneLeftWrong() {
        setOne.put("four", new DigestResult(new Digest("crc", "2222")));
        setTwo.put("four", new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setTwo.exclude(setOne);
        assertEquals(4, setTwo.size());
        assertEquals(4, setOne.size());
        assertEquals(1, result.size());

        assertTrue(result.containsKey("four"));
        assertEquals("3333", result.get("four").digest().toHex());
    }

    @Test
    public void testExcludeEmptySet() {
        setTwo.clear();

        DirHasherResult result = setTwo.exclude(setOne);
        assertEquals(0, setTwo.size());
        assertEquals(3, setOne.size());
        assertEquals(0, result.size());

        result = setOne.exclude(setTwo);
        assertEquals(0, setTwo.size());
        assertEquals(3, setOne.size());
        assertEquals(3, result.size());
        assertEquals(setOne, result);
    }

    @Test
    public void testEquals() {
        assertTrue(setOne != setTwo);
        assertEquals(setOne, setTwo);
    }
    
    @Test
    public void testIntersect() {
        DirHasherResult result = setOne.intersect(setTwo);

        assertEquals(3, result.size());
        assertEquals(setOne, result);
    }

    @Test
    public void testIntersectTwo() {
        setOne.put("four", new DigestResult(new Digest("crc", "2222")));
        setTwo.put("four", new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setOne.intersect(setTwo);

        assertEquals(3, result.size());
        assertTrue(! result.containsKey("four"));
    }

    @Test
    public void testIncludeWrong() {
        setOne.put("four", new DigestResult(new Digest("crc", "2222")));
        setTwo.put("four", new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setOne.includeWrong(setTwo);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("four"));
    }

    @Test
    public void testNotIntersect() {
        setOne.put("four", new DigestResult(new Digest("crc", "2222")));
        setTwo.put("four", new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setOne.includeWrong(setTwo);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("four"));
    }

}