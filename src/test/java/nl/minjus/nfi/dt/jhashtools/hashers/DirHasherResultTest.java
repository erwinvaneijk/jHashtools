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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 * 
 * @author Erwin van Eijk
 */
public class DirHasherResultTest
{

    public DirHasherResult setOne;
    public DirHasherResult setTwo;

    public DirHasherResultTest()
    {
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
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

    @AfterEach
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
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void testExcludeOneLeft() {
        setTwo.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        DirHasherResult result = setTwo.exclude(setOne);
        assertThat(result.size()).isEqualTo(1);
        assertThat(setTwo.size()).isEqualTo(4);
        assertThat(setOne.size()).isEqualTo(3);
        assertThat(result.containsKey(new File("four"))).isTrue();
    }

    @Test
    public void testExcludeOneLeftOtherSize() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        DirHasherResult result = setTwo.exclude(setOne);
        assertThat(result.size()).isEqualTo(0);
        assertThat(setTwo.size()).isEqualTo(3);
        assertThat(setOne.size()).isEqualTo(4);
    }

    @Test
    public void testExcludeOneLeftWrong() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File("four"), new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setTwo.exclude(setOne);
        assertThat(setTwo.size()).isEqualTo(4);
        assertThat(setOne.size()).isEqualTo(4);
        assertThat(result.size()).isEqualTo(1);

        assertThat(result.containsKey("four")).isTrue();
        assertThat(result.get("four").digest().toHex()).isEqualTo("3333");
    }

    @Test
    public void testExcludeEmptySet() {
        setTwo.clear();

        DirHasherResult result = setTwo.exclude(setOne);
        assertThat(setTwo.size()).isEqualTo(0);
        assertThat(setOne.size()).isEqualTo(3);
        assertThat(result.size()).isEqualTo(0);

        result = setOne.exclude(setTwo);
        assertThat(setTwo.size()).isEqualTo(0);
        assertThat(setOne.size()).isEqualTo(3);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).isEqualTo(setOne);
    }

    @Test
    public void testEquals() {
        assertThat(setOne).isNotSameAs(setTwo);
        assertThat(setTwo).isEqualTo(setOne);
        assertThat(setOne).isEqualTo(setTwo);
    }

    @Test
    public void testIntersect() {
        DirHasherResult result = setOne.intersect(setTwo);

        assertThat(result.size()).isEqualTo(3);
        assertThat(result).isEqualTo(setOne);
        assertThat(setOne).isEqualTo(result);
    }

    @Test
    public void testEqualityWhileNamesAlmostDifferent() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File("." + File.separator + "four"), new DigestResult(new Digest("crc", "2222")));

        setOne.put(new File("." + File.separator + "five"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File(System.getProperty("user.dir") + File.separator + "five"), new DigestResult(
            new Digest("crc", "2222")));

        assertThat(setTwo).isEqualTo(setOne);
    }

    @Test
    public void testNotIntersect() {
        DirHasherResult result = setOne.notIntersect(setTwo);

        assertThat(result.size()).isEqualTo(0);

        result = setTwo.notIntersect(setOne);
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void testNotIntersectTwo() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File("five"), new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setOne.notIntersect(setTwo);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.containsKey("four")).isTrue();
        assertThat(result.containsKey("five")).isTrue();

        DirHasherResult result2 = setTwo.notIntersect(setOne);
        assertThat(result2).isEqualTo(result);
    }

    @Test
    public void testIntersectTwo() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File("four"), new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setOne.intersect(setTwo);

        assertThat(result.size()).isEqualTo(3);
        assertThat(!result.containsKey(new File("four"))).isTrue();
        assertThat(result.get(new File("three")).containsResult("crc")).isTrue();
        assertThat(result.get(new File("three")).getDigest("crc")).isEqualTo(new Digest("crc", "2222"));

        DirHasherResult result2 = setTwo.intersect(setOne);
        assertThat(result2).isEqualTo(result);
    }

    @Test
    public void testIntersectTwoDifferentSets() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File("five"), new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setOne.intersect(setTwo);

        assertThat(result.size()).isEqualTo(3);
        assertThat(!result.containsKey(new File("four"))).isTrue();
        assertThat(!result.containsKey(new File("five"))).isTrue();

        DirHasherResult result2 = setTwo.intersect(setOne);
        assertThat(result2).isEqualTo(result);
    }

    @Test
    public void testIntersectTwoMultipleHits() {
        Collection<Digest> list = new ArrayList<Digest>();
        list.add(new Digest("crc", "eeee"));
        list.add(new Digest("md4", "1111"));
        setOne.put(new File("four"), new DigestResult(list));
        List<Digest> list2 = new ArrayList<Digest>();
        list2.add(new Digest("md4", "1111"));
        setTwo.put(new File("four"), new DigestResult(list2));

        DirHasherResult result = setOne.intersect(setTwo);

        assertThat(result.size()).isEqualTo(3);
        assertThat(!result.containsKey("four")).isTrue();
        assertThat(!result.containsKey("five")).isTrue();
        assertThat(result.get("three").containsResult("crc")).isTrue();

        DirHasherResult result2 = setTwo.intersect(setOne);
        assertThat(result2).isEqualTo(result);
    }

    @Test
    public void testIncludeWrong() {
        setOne.put("four", new DigestResult(new Digest("crc", "2222")));
        setTwo.put("four", new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setOne.includeWrong(setTwo);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.containsKey("four")).isTrue();
    }

    @Test
    public void testNotIntersectSameDifferent() {
        setOne.put("four", new DigestResult(new Digest("crc", "2222")));
        setTwo.put("four", new DigestResult(new Digest("crc", "3333")));

        DirHasherResult result = setOne.notIntersect(setTwo);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.containsKey("four")).isTrue();
    }

    @Test
    public void testMissing() {
        setOne.put(new File("four"), new DigestResult(new Digest("crc", "2222")));
        setTwo.put(new File("five"), new DigestResult(new Digest("crc", "3333")));

        // get all the entries that are in one, but not in two.
        DirHasherResult result = setOne.missing(setTwo);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.containsKey("four")).isTrue();
        assertThat(!result.containsKey("five")).isTrue();
    }
}
