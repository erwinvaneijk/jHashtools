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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import nl.minjus.nfi.dt.jhashtools.DigestResult;

/**
 * Created by IntelliJ IDEA. User: eijk Date: May 22, 2010 Time: 5:45:24 PM To change this template use File | Settings
 * | File Templates.
 */
public class ConcurrentFileHasherTest
{
    @Test
    public void testGetDigest() throws NoSuchAlgorithmException, IOException {
        final String content = "foo";
        final ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        final FileHasher serialFileHasher = new ConcurrentFileHasher(DigestAlgorithmFactory.create("sha-256"));
        final DigestResult result = serialFileHasher.getDigest(stream);

        assertThat(result
            .getDigest("sha-256").toString())
            .isEqualTo("sha-256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae");
    }

    @Test
    public void testGetDigestOfFileSerially() throws NoSuchAlgorithmException, IOException {
        final InputStream stream = new FileInputStream(new File("testdata/include-sha1-sha256-sha512.txt"));
        final Collection<DigestAlgorithm> algorithms = new LinkedList<DigestAlgorithm>();
        algorithms.add(DigestAlgorithmFactory.create("sha-1"));
        algorithms.add(DigestAlgorithmFactory.create("md5"));
        final FileHasher fileHasher = new SerialFileHasher(algorithms);
        final DigestResult result = fileHasher.getDigest(stream);

        assertThat(result.getDigest("sha-1").toString()).as("sha1 do not match")
            .isEqualTo("sha-1:2b3a601a1ee759eec30ddcde458d459aa26ba78f");

        assertThat(result.getDigest("md5")
            .toString()).as("md5 do not match").isEqualTo("md5:bed8e0d55ab120d38325af63da19125f");
    }

    @Test
    public void testGetTwoDigestsOfFileConcurrent() throws NoSuchAlgorithmException, IOException {
        final InputStream stream = new FileInputStream(new File("testdata/include-sha1-sha256-sha512.txt"));
        final Collection<DigestAlgorithm> algorithms = new LinkedList<DigestAlgorithm>();
        algorithms.add(DigestAlgorithmFactory.create("sha-1"));
        algorithms.add(DigestAlgorithmFactory.create("md5"));
        final FileHasher fileHasher = new ConcurrentFileHasher(algorithms);
        final DigestResult result = fileHasher.getDigest(stream);

        assertThat(result.getDigest("sha-1").toString()).as("sha1 do not match")
            .isEqualTo("sha-1:2b3a601a1ee759eec30ddcde458d459aa26ba78f");

        assertThat(result.getDigest("md5")
            .toString()).as("md5 do not match").isEqualTo("md5:bed8e0d55ab120d38325af63da19125f");
    }

    @Test
    public void testGetDigestOfFileConcurrent() throws NoSuchAlgorithmException, IOException {
        final InputStream stream = new FileInputStream(new File("testdata/include-sha1-sha256-sha512.txt"));
        final Collection<DigestAlgorithm> algorithms = new LinkedList<DigestAlgorithm>();
        algorithms.add(DigestAlgorithmFactory.create("md5"));
        final FileHasher fileHasher = new ConcurrentFileHasher(algorithms);
        final DigestResult result = fileHasher.getDigest(stream);

        assertThat(result.getDigest("md5")
            .toString()).as("md5 do not match").isEqualTo("md5:bed8e0d55ab120d38325af63da19125f");
    }
}
