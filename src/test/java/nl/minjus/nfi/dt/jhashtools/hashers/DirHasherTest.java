/*
 * Copyright (c) 2010 Erwin van Eijk <erwin.vaneijk@gmail.com>.
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
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.hashers.actors.ActingDirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.utils.AllowedFailure;
import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;

/**
 * Test the serial dirHasher.
 * 
 * @author Erwin van Eijk
 */
@ParameterizedClass(name = "{index}: hasher:{0}")
@MethodSource("data")
public class DirHasherTest
{

    public static Collection<Object[]> data() {
        Collection<Object[]> collection = new LinkedList<Object[]>();
        try {
            collection.add(new Object[] { new SerialDirectoryHasher("sha-256"),
                "sha-256" });
            collection.add(new Object[] { new ActingDirectoryHasher("sha-256"),
                "sha-256" });
            collection.add(new Object[] { new SerialDirectoryHasher("sha-1"),
                "sha-1" });
            collection.add(new Object[] { new ActingDirectoryHasher("sha-1"),
                "sha-1" });
            collection.add(new Object[] { new SerialDirectoryHasher("md5"),
                "md5" });
            collection.add(new Object[] { new ActingDirectoryHasher("md5"),
                "md5" });
        } catch (NoSuchAlgorithmException e) {
            fail("All the algorithms should exist");
        }
        return collection;
    }

    private static DirHasherResult knownDigests;

    private DirectoryHasher directoryHasher;

    private String algorithm;

    public DirHasherTest(DirectoryHasher directoryHasher, String algorithm)
    {
        this.directoryHasher = directoryHasher;
        this.algorithm = algorithm;
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
        knownDigests = KnownDigests.getKnownResults();
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @AllowedFailure(AssertionError.class)
    @Test
    public void testGetDigests() {
        final DirHasherResult digests =
            directoryHasher.getDigests(new File("testdata"));
        assertThat(digests.size()).isEqualTo(knownDigests.size());
        final DirHasherResult knownDigestSha256 = knownDigests
            .getByAlgorithm(this.algorithm);
        assertThat(digests.intersect(knownDigestSha256)).isEqualTo(knownDigestSha256);
        assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
    }

    @AllowedFailure(AssertionError.class)
    @Test
    public void testUpdateDigests() {
        final DirHasherResult digests = new DirHasherResult();
        directoryHasher.updateDigests(digests, new File("testdata"));
        assertThat(digests.size()).isEqualTo(knownDigests.size());
        final DirHasherResult knownDigestSha256 = knownDigests
            .getByAlgorithm(algorithm);
        assertThat(digests.intersect(knownDigestSha256)).isEqualTo(knownDigestSha256);
        assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
    }

    @Test
    public void testUpdateDigestsUnknownDirectory() {
        try {
            final DirHasherResult digests = new DirHasherResult();
            directoryHasher.updateDigests(digests, new File(
                "does-not-exist-testdata"));
            fail("An IllegalArgumentException should have been thrown");
        } catch (final IllegalArgumentException ex) {
            // this is ok!
        }
    }

    @Test
    public void testVerboseSettings() {
        assertThat(directoryHasher.isVerbose()).as("initially no verbose behaviour").isEqualTo(false);
        directoryHasher.setVerbose(true);
        assertThat(directoryHasher.isVerbose()).isTrue();
    }

    @Test
    public void testGetDigestsOtherWayAround() {
        final DirHasherResult digests =
            directoryHasher.getDigests(new File("testdata"));
        assertThat(digests.size()).isEqualTo(knownDigests.size());
        final DirHasherResult knownDigestSha256 =
            knownDigests.getByAlgorithm(algorithm);
        assertThat(digests.intersect(knownDigestSha256)).isEqualTo(knownDigestSha256);
        assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
        assertThat(knownDigests.intersect(digests)).isEqualTo(digests);
    }

    @Test
    public void testGetDigestsExclude() {
        final DirHasherResult digests =
            directoryHasher.getDigests(new File("testdata"));
        assertThat(digests.size()).isEqualTo(knownDigests.size());
        final DirHasherResult knownDigestSha256 =
            knownDigests.getByAlgorithm(algorithm);
        assertThat(digests.exclude(knownDigestSha256).size()).isEqualTo(0);
    }

    @Test
    public void testGetTwoOutOfThreeDigests() {
        try {
            final DirectoryHasher directoryHasher = new SerialDirectoryHasher(
                "md5");
            directoryHasher.addAlgorithm("sha-256");
            final DirHasherResult digests = directoryHasher
                .getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
            final DirHasherResult knownDigestMd5 = knownDigests
                .getByAlgorithm("md5");
            DirHasherResult intersected = digests.intersect(knownDigestMd5);
            assertThat(intersected).isEqualTo(knownDigestMd5);

            final DirHasherResult knownDigestSha256 = knownDigests
                .getByAlgorithm("sha-256");
            intersected = digests.intersect(knownDigestSha256);
            assertThat(intersected).isEqualTo(knownDigestSha256);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetOneOutOfThreeDigests() {
        try {
            final DirectoryHasher directoryHasher = new SerialDirectoryHasher(
                "sha-256");

            final DirHasherResult digests =
                directoryHasher.getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
            final DirHasherResult knownDigestMd5 =
                knownDigests.getByAlgorithm("md5");
            assertThat(knownDigestMd5.size()).isEqualTo(13);

            DirHasherResult intersected = digests.intersect(knownDigestMd5);
            assertThat(intersected.size()).isEqualTo(0);

            final DirHasherResult knownDigestSha256 = knownDigests
                .getByAlgorithm("sha-256");
            intersected = digests.intersect(knownDigestSha256);
            assertThat(intersected).isEqualTo(knownDigestSha256);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @Test
    public void testGetThreeOutOfThreeDigests() {
        try {
            final DirectoryHasher directoryHasher = new SerialDirectoryHasher(
                "sha-256");
            directoryHasher.addAlgorithm("md5");
            directoryHasher.addAlgorithm("sha-1");
            final DirHasherResult digests = directoryHasher
                .getDigests(new File("testdata"));
            assertThat(digests.size()).isEqualTo(knownDigests.size());
            assertThat(digests.intersect(knownDigests)).isEqualTo(digests);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void testGetDirectoryDigestRaisedIllegalArgument() {
        try {
            final DirHasherResult digests = directoryHasher
                .getDigests(new File("does-not-exist"));
            fail("We should not get here. An exception should have been thrown");
        } catch (final IllegalArgumentException ex) {
            // everything is ok.
        }
    }
}
