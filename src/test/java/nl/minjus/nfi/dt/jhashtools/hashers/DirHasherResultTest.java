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

package nl.minjus.nfi.dt.jhashtools.hashers;

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import org.junit.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Erwin van Eijk
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
        setOne.put(new File("one"), new DigestResult(new Digest("crc", "1111")));
        setOne.put(new File("two"), new DigestResult(new Digest("crc", "1111")));
        setOne.put(new File("three"), new DigestResult(new Digest("crc", "2222")));

        setTwo = new DirHasherResult();
        setTwo.put(new File("one"), new DigestResult(new Digest("crc", "1111")));
        setTwo.put(new File("two"), new DigestResult(new Digest("crc", "1111")));
        setTwo.put(new File("three"), new DigestResult(new Digest("crc", "2222")));
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
    public void testExcludeOneLeft() {
        setTwo.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        DirHasherResult result = setTwo.exclude(setOne);
        assertEquals(1, result.size());
        assertEquals(4, setTwo.size());
        assertEquals(3, setOne.size());
        assertTrue(result.containsKey(new File("four")));
    }

    @Test
    public void testExcludeOneLeftOtherSize() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        DirHasherResult result = setTwo.exclude(setOne);
        assertEquals(0, result.size());
        assertEquals(3, setTwo.size());
        assertEquals(4, setOne.size());
    }

    @Test
    public void testExcludeOneLeftWrong() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File("four"), new DigestResult(new Digest("crc", "3333")));

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
        assertEquals(setTwo, setOne);
    }
    
    @Test
    public void testIntersect() {
        DirHasherResult result = setOne.intersect(setTwo);

        assertEquals(3, result.size());
        assertEquals(setOne, result);
        assertEquals(result, setOne);
    }

    @Test
    public void testEqualityWhileNamesAlmostDifferent() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File("." + File.separator + "four"), new DigestResult(new Digest("crc", "2222")));

        setOne.put(new File("." + File.separator + "five"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File(System.getProperty("user.dir") + File.separator + "five"), new DigestResult(new Digest("crc", "2222")));

        assertEquals(setOne, setTwo);
    }

    @Test
    public void testNotIntersect() {
        DirHasherResult result = setOne.notIntersect(setTwo);

        assertEquals(0, result.size());

        result = setTwo.notIntersect(setOne);
        assertEquals(0, result.size());
    }

    @Test
    public void testNotIntersectTwo() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File("five"), new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setOne.notIntersect(setTwo);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("four"));
        assertTrue(result.containsKey("five"));

        DirHasherResult result2 = setTwo.notIntersect(setOne);
        assertEquals(result, result2);
    }

    @Test
    public void testIntersectTwo() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File("four"), new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setOne.intersect(setTwo);

        assertEquals(3, result.size());
        assertTrue(! result.containsKey(new File("four")));
        assertTrue(result.get(new File("three")).containsResult("crc"));
        assertEquals(new Digest("crc", "2222"), result.get(new File("three")).getDigest("crc"));

        DirHasherResult result2 = setTwo.intersect(setOne);
        assertEquals(result, result2);
    }

    @Test
    public void testIntersectTwoDifferentSets() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File("five"), new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setOne.intersect(setTwo);

        assertEquals(3, result.size());
        assertTrue(! result.containsKey(new File("four")));
        assertTrue(! result.containsKey(new File("five")));

        DirHasherResult result2 = setTwo.intersect(setOne);
        assertEquals(result, result2);
    }

    @Test
    public void testIntersectTwoMultipleHits() {
        List<Digest> list = new ArrayList<Digest>();
        list.add( new Digest("crc", "eeee") );
        list.add( new Digest("md4", "1111") );
        setOne.put(new File("four"), new DigestResult(list));
        List<Digest> list2 = new ArrayList<Digest>();
        list.add( new Digest("md4", "1111") );
        setTwo.put(new File("four"), new DigestResult(list2));

        DirHasherResult result = setOne.intersect(setTwo);

        assertEquals(3, result.size());
        assertTrue(! result.containsKey("four"));
        assertTrue(! result.containsKey("five"));
        assertTrue(result.get("three").containsResult("crc"));

        DirHasherResult result2 = setTwo.intersect(setOne);
        assertEquals(result, result2);
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
    public void testNotIntersectSameDifferent() {
        setOne.put("four", new DigestResult(new Digest("crc", "2222")));
        setTwo.put("four", new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setOne.notIntersect(setTwo);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("four"));
    }

        @Test
    public void testMissing() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File("five"), new DigestResult(new Digest("crc", "3333")));

        // get all the entries that are in one, but not in two.
        DirHasherResult result = setOne.missing(setTwo);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("four"));
        assertTrue(! result.containsKey("five"));
    }
}
