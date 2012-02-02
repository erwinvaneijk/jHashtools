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

import static junit.framework.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;

import nl.minjus.nfi.dt.jhashtools.DigestResult;

import org.junit.Test;

/**
 * Created by IntelliJ IDEA. User: eijk Date: May 22, 2010 Time: 5:45:24 PM To change this template use File | Settings
 * | File Templates.
 */
public class ConcurrentFileHasherTest
{
    @Test
    public void testGetDigest() throws NoSuchAlgorithmException, IOException {
        final String content = "foo";
        final ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes());
        final FileHasher serialFileHasher = new ConcurrentFileHasher(DigestAlgorithmFactory.create("sha-256"));
        final DigestResult result = serialFileHasher.getDigest(stream);

        assertEquals("sha-256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae", result
            .getDigest("sha-256").toString());
    }

    @Test
    public void testGetDigestOfFileSerially() throws NoSuchAlgorithmException, IOException {
        final InputStream stream = new FileInputStream(new File("testdata/include-sha1-sha256-sha512.txt"));
        final Collection<DigestAlgorithm> algorithms = new LinkedList<DigestAlgorithm>();
        algorithms.add(DigestAlgorithmFactory.create("sha-1"));
        algorithms.add(DigestAlgorithmFactory.create("md5"));
        final FileHasher fileHasher = new SerialFileHasher(algorithms);
        final DigestResult result = fileHasher.getDigest(stream);

        assertEquals("sha1 do not match", "sha-1:2b3a601a1ee759eec30ddcde458d459aa26ba78f",
            result.getDigest("sha-1").toString());

        assertEquals("md5 do not match", "md5:bed8e0d55ab120d38325af63da19125f", result.getDigest("md5")
            .toString());
    }

    @Test
    public void testGetTwoDigestsOfFileConcurrent() throws NoSuchAlgorithmException, IOException {
        final InputStream stream = new FileInputStream(new File("testdata/include-sha1-sha256-sha512.txt"));
        final Collection<DigestAlgorithm> algorithms = new LinkedList<DigestAlgorithm>();
        algorithms.add(DigestAlgorithmFactory.create("sha-1"));
        algorithms.add(DigestAlgorithmFactory.create("md5"));
        final FileHasher fileHasher = new ConcurrentFileHasher(algorithms);
        final DigestResult result = fileHasher.getDigest(stream);

        assertEquals("sha1 do not match", "sha-1:2b3a601a1ee759eec30ddcde458d459aa26ba78f",
            result.getDigest("sha-1").toString());

        assertEquals("md5 do not match", "md5:bed8e0d55ab120d38325af63da19125f", result.getDigest("md5")
            .toString());
    }

    @Test
    public void testGetDigestOfFileConcurrent() throws NoSuchAlgorithmException, IOException {
        final InputStream stream = new FileInputStream(new File("testdata/include-sha1-sha256-sha512.txt"));
        final Collection<DigestAlgorithm> algorithms = new LinkedList<DigestAlgorithm>();
        algorithms.add(DigestAlgorithmFactory.create("md5"));
        final FileHasher fileHasher = new ConcurrentFileHasher(algorithms);
        final DigestResult result = fileHasher.getDigest(stream);

        assertEquals("md5 do not match", "md5:bed8e0d55ab120d38325af63da19125f", result.getDigest("md5")
            .toString());
    }
}
