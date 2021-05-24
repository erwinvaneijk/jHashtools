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

package nl.minjus.nfi.dt.jhashtools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import nl.minjus.nfi.dt.jhashtools.hashers.ConcurrencyMode;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasherCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProvider;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProviderCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceStyle;

public class IntegrationTest
{

    @Test
    public void testIncludeFiles() throws IOException, PersistenceException {
        Reader reader = new FileReader("testdata/include-md5-sha1.txt");
        final PersistenceProvider provider = PersistenceProviderCreator.create(PersistenceStyle.JSON);
        final DirHasherResult setOne = provider.load(reader, DirHasherResult.class);
        assertEquals(3976, setOne.size());

        reader = new FileReader("testdata/include-sha1-sha256-sha512.txt");
        final DirHasherResult setTwo = provider.load(reader, DirHasherResult.class);
        assertEquals(3976, setTwo.size());

        final DirHasherResult setOneSha1 = setOne.getByAlgorithm("sha-1");
        final DirHasherResult setTwoSha1 = setTwo.getByAlgorithm("sha-1");
        DirHasherResult diff = setOneSha1.exclude(setTwoSha1);
        assertEquals(0, diff.size());

        assert setOne.matches(setTwo);
        assert setTwo.matches(setOne);

        diff = setOne.exclude(setTwo);
        assertEquals(0, diff.size());

        diff = setOne.includeWrong(setTwo);
        assertEquals(0, diff.size());

        diff = setTwo.includeWrong(setOne);
        assertEquals(0, diff.size());

        diff = setOne.intersect(setTwo);
        assert setOne.matches(diff);
        assert diff.matches(setTwo);

        diff = setOne.notIntersect(setTwo);
        assertEquals(0, diff.size());
    }

    // @Test(timeout=120000)
    public void testLargeTree() {
        try {
            final Collection<String> digestAlgorithms = new ArrayList<String>();
            digestAlgorithms.add("sha-256");
            digestAlgorithms.add("md5");
            digestAlgorithms.add("sha-1");
            final DirectoryHasher directoryHasher = DirectoryHasherCreator.create(ConcurrencyMode.MULTI_THREADING,
                digestAlgorithms);
            final DirHasherResult digests = directoryHasher
                .getDigests(new File("/Users/eijk/Sources/boost_1_42_0"));
            assert digests.size() > 0;

            final Reader reader = new FileReader("boost-hashes.txt");
            final PersistenceProvider provider = PersistenceProviderCreator.create(PersistenceStyle.JSON);
            final DirHasherResult setOne = provider.load(reader, DirHasherResult.class);

            assert setOne.matches(digests);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        } catch (final IOException ex) {
            fail(ex.toString() + " should not happen");
        } catch (final PersistenceException ex) {
            fail(ex.toString() + " should not happen");
        }
    }

    // @Test(timeout=120000)
    public void testLargeTreeConcurrent() {
        try {
            final Collection<String> algorithms = new LinkedList<String>();
            algorithms.add("sha-256");
            algorithms.add("md5");
            algorithms.add("sha-1");
            final DirectoryHasher directoryHasher = DirectoryHasherCreator.create(ConcurrencyMode.MULTI_THREADING,
                algorithms);
            final DirHasherResult digests = directoryHasher
                .getDigests(new File("/Users/eijk/Sources/boost_1_42_0"));
            assert digests.size() > 0;

            final Reader reader = new FileReader("boost-hashes.txt");
            final PersistenceProvider provider = PersistenceProviderCreator.create(PersistenceStyle.JSON);
            final DirHasherResult setOne = provider.load(reader, DirHasherResult.class);

            assert setOne.matches(digests);
        } catch (final NoSuchAlgorithmException ex) {
            fail(ex.toString() + " should not happen");
        } catch (final IOException ex) {
            fail(ex.toString() + " should not happen");
        } catch (final PersistenceException ex) {
            fail(ex.toString() + " should not happen");
        }
    }
}
