/*
 * Copyright (c) 2010 Erwin van Eijk.
 *
 * Licensed under the EUPL, Version 1.2 only (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific language
 * governing permissions and limitations under the Licence.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package nl.minjus.nfi.dt.jhashtools.hashers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.minjus.nfi.dt.jhashtools.AccessTimes;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.hashers.actors.ActingDirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;

/**
 */
public class ActorDirectoryHasherTest
{
    private DirHasherResult knownDigests = null;

    public ActorDirectoryHasherTest()
    {
        super();
    }

    @BeforeEach
    public void setUp() {
        knownDigests = KnownDigests.getKnownResults();
    }

    @Test
    public void testGetDigests() {
        try {
            final DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            final DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            final DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            final DirHasherResult result = digests.intersect(knownDigestSha256);
            assertThat(result).isEqualTo(knownDigestSha256);
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
            for (final Map.Entry<File, DigestResult> digest : digests) {
                final int count = digests.count(digest.getValue());
                assertThat(count).isEqualTo(1);
            }
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testUpdateDigests() {
        try {
            final DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            final DirHasherResult digests = new DirHasherResult();
            directoryHasher.updateDigests(digests, new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            final DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertThat(digests.intersect(knownDigestSha256)).isEqualTo(knownDigestSha256);
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetDigestsOtherWayAround() {
        try {
            final DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            final DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            final DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertThat(digests.intersect(knownDigestSha256)).isEqualTo(knownDigestSha256);
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
            final DirHasherResult d2 = knownDigests.intersect(digests);
            assertThat(d2).isEqualTo(knownDigestSha256);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetDigestsExclude() {
        try {
            final DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            final DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            final DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertThat(digests.exclude(knownDigestSha256).size()).isEqualTo(0);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetMd5Digests() {
        try {
            final DirectoryHasher directoryHasher = new ActingDirectoryHasher("md5");
            final DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            final DirHasherResult knownDigestMd5 = knownDigests.getByAlgorithm("md5");
            assertThat(digests.intersect(knownDigestMd5)).isEqualTo(knownDigestMd5);
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetShaDigests() {
        try {
            final DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-1");
            final DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            final DirHasherResult knownDigestSha = knownDigests.getByAlgorithm("sha-1");
            assertThat(digests.intersect(knownDigestSha)).isEqualTo(knownDigestSha);
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetTwoOutOfThreeDigests() {
        try {
            final DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            directoryHasher.addAlgorithm("md5");
            final DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            final DirHasherResult knownDigestSha256 = knownDigests.getByAlgorithm("sha-256");
            assertThat(digests.intersect(knownDigestSha256)).isEqualTo(knownDigestSha256);
            final DirHasherResult remainingDigests = digests.intersect(knownDigests);
            assertThat(remainingDigests).isEqualTo(digests);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetThreeOutOfThreeDigests() {
        try {
            final DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            directoryHasher.addAlgorithm("sha-1");
            directoryHasher.addAlgorithm("md5");
            final DirHasherResult digests = directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            final DirHasherResult remainingDigests = digests.intersect(knownDigests);
            assertThat(remainingDigests).isEqualTo(digests);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void testGetDirectoryDigestRaisedIllegalArgument() {
        try {
            final DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");
            final DirHasherResult digests = directoryHasher.getDigests(new File("does-not-exist"));
            fail("We should not get here. An exception should have been thrown");
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        } catch (final IllegalArgumentException ex) {
            // everything is ok.
        }
    }

    @Test
    public void testRecordsAccessTimes(@TempDir final Path tempFolder) throws Exception {
        final FileTime knownAccessTime = FileTime.from(Instant.parse("2001-02-03T04:05:06Z"));
        final Path file = Files.writeString(tempFolder.resolve("file.txt"), "content");
        Files.getFileAttributeView(file, BasicFileAttributeView.class).setTimes(null, knownAccessTime, null);
        final DirectoryHasher directoryHasher = new ActingDirectoryHasher("sha-256");

        final DirHasherResult digests = directoryHasher.getDigests(tempFolder.toFile());

        final AccessTimes accessTimes = digests.getAccessTimes().get(file.toFile());
        assertThat(accessTimes.before()).isEqualTo(knownAccessTime);
        assertThat(accessTimes.after()).isEqualTo(AccessTimes.lastAccessTime(file.toFile()));
    }
}
