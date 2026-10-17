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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

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
}
