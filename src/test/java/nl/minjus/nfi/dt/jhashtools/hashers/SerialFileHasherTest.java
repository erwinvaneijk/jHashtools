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
import static org.assertj.core.api.Assumptions.assumeThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import com.sun.management.UnixOperatingSystemMXBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.minjus.nfi.dt.jhashtools.DigestResult;

/**
 * @author Erwin van Eijk
 */
public class SerialFileHasherTest
{
    @Test
    public void testGetDigest() throws NoSuchAlgorithmException, IOException {
        String content = "foo";
        ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        SerialFileHasher serialFileHasher = new SerialFileHasher(DigestAlgorithmFactory.create("sha-256"));
        DigestResult result = serialFileHasher.getDigest(stream);

        assertThat(result
            .getDigest("sha-256").toString())
            .isEqualTo("sha-256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae");
    }

    @Test
    public void testGetRipemd160Digest() throws NoSuchAlgorithmException, IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream("abc".getBytes(StandardCharsets.UTF_8));
        final SerialFileHasher serialFileHasher = new SerialFileHasher(DigestAlgorithmFactory.create("ripemd160"));

        final DigestResult result = serialFileHasher.getDigest(stream);

        // Test vector from the RIPEMD-160 specification.
        assertThat(result.getDigest("ripemd160").toString())
            .isEqualTo("ripemd160:8eb208f7e05d987a9b044a8e98c6b087f15a0bfc");
    }

    @Test
    public void testGetDigestOfFileClosesTheFile(@TempDir final Path tempDir) throws NoSuchAlgorithmException,
        IOException
    {
        final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        assumeThat(osBean).isInstanceOf(UnixOperatingSystemMXBean.class);
        final UnixOperatingSystemMXBean unixOsBean = (UnixOperatingSystemMXBean) osBean;
        final Path file = Files.writeString(tempDir.resolve("foo.txt"), "foo", StandardCharsets.UTF_8);
        final SerialFileHasher serialFileHasher = new SerialFileHasher(DigestAlgorithmFactory.create("sha-256"));
        serialFileHasher.getDigest(file.toFile());
        final long openFilesBefore = unixOsBean.getOpenFileDescriptorCount();

        serialFileHasher.getDigest(file.toFile());

        assertThat(unixOsBean.getOpenFileDescriptorCount()).isLessThanOrEqualTo(openFilesBefore);
    }
}
