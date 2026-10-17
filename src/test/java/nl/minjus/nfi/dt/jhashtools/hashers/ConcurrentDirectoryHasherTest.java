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
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.hashers.actors.ActingDirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;

/**
 */
public class ConcurrentDirectoryHasherTest
{
    private DirHasherResult knownDigests = null;

    @BeforeEach
    public void setUp() {
        knownDigests = KnownDigests.getKnownResults();
    }

    @Test
    public void testGetDigests() {
        try {
            DirectoryHasher directoryHasher = DirectoryHasherCreator.create(ConcurrencyMode.MULTI_THREADING, Arrays.asList("sha-256"));
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            DirHasherResult result = digests.intersect(knownDigestSha256);
            assertThat(result).isEqualTo(knownDigestSha256);
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
            for (Map.Entry<File, DigestResult> digest : digests) {
                int count = digests.count(digest.getValue());
                assertThat(count).isEqualTo(1);
            }
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetMultipleDigests() {
        try {
            Collection<String> algorithms = new LinkedList<String>();
            algorithms.add("sha-256");
            DirectoryHasher directoryHasher = new ActingDirectoryHasher(algorithms);
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            DirHasherResult result = digests.intersect(knownDigestSha256);
            assertThat(result).isEqualTo(knownDigestSha256);
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
            for (Map.Entry<File, DigestResult> digest : digests) {
                int count = digests.count(digest.getValue());
                assertThat(count).isEqualTo(1);
            }
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }
    
    @Test
    public void testUpdateDigests() {
        try {
            DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            DirHasherResult digests = new DirHasherResult();
            directoryHasher.updateDigests(digests, new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertThat(digests.intersect(knownDigestSha256)).isEqualTo(knownDigestSha256);
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testVerboseSettings() {
        try {
            DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            assertThat(directoryHasher.isVerbose()).as("initially no verbose behaviour").isEqualTo(false);
            directoryHasher.setVerbose(true);
            assertThat(directoryHasher.isVerbose()).isTrue();
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetDigestsOtherWayAround() {
        try {
            DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertThat(digests.intersect(knownDigestSha256)).isEqualTo(knownDigestSha256);
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
            assertThat(knownDigests.intersect(digests)).isEqualTo(digests);
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetDigestsExclude() {
        try {
            DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertThat(digests.exclude(knownDigestSha256).size()).isEqualTo(0);
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetMd5Digests() {
        try {
            DirectoryHasher directoryHasher = new ActingDirectoryHasher("md5");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            DirHasherResult knownDigestMd5 = knownDigests.getByAlgorithm("md5");
            assertThat(digests.intersect(knownDigestMd5)).isEqualTo(knownDigestMd5);
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetShaDigests() {
        try {
            DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-1");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            DirHasherResult knownDigestSha = knownDigests.getByAlgorithm("sha-1");
            assertThat(digests.intersect(knownDigestSha)).isEqualTo(knownDigestSha);
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetTwoOutOfThreeDigests() {
        try {
            DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            directoryHasher.addAlgorithm("md5");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertThat(digests.intersect(knownDigestSha256)).isEqualTo(knownDigestSha256);
            DirHasherResult remainingDigests = digests.intersect(knownDigests);
            assertThat(remainingDigests).isEqualTo(digests);
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetThreeOutOfThreeDigests() {
        try {
            DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            directoryHasher.addAlgorithm("sha-1");
            directoryHasher.addAlgorithm("md5");
            DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            DirHasherResult remainingDigests = digests.intersect(knownDigests);
            assertThat(remainingDigests).isEqualTo(digests);
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void testGetDirectoryDigestRaisedIllegalArgument() {
        try {
            DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            DirHasherResult digests = directoryHasher.getDigests(new File("does-not-exist"));
            fail("We should not get here. An exception should have been thrown");
        } catch (NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        } catch (IllegalArgumentException ex) {
            // everything is ok.
        }
    }
}
