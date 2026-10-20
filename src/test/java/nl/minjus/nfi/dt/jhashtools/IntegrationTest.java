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

package nl.minjus.nfi.dt.jhashtools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

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
        Reader reader = new FileReader("testdata/include-md5-sha1.txt", StandardCharsets.UTF_8);
        final PersistenceProvider provider = PersistenceProviderCreator.create(PersistenceStyle.JSON);
        final DirHasherResult setOne = provider.load(reader, DirHasherResult.class);
        assertThat(setOne.size()).isEqualTo(3976);

        reader = new FileReader("testdata/include-sha1-sha256-sha512.txt", StandardCharsets.UTF_8);
        final DirHasherResult setTwo = provider.load(reader, DirHasherResult.class);
        assertThat(setTwo.size()).isEqualTo(3976);

        final DirHasherResult setOneSha1 = setOne.getByAlgorithm("sha-1");
        final DirHasherResult setTwoSha1 = setTwo.getByAlgorithm("sha-1");
        DirHasherResult diff = setOneSha1.exclude(setTwoSha1);
        assertThat(diff.size()).isEqualTo(0);

        assert setOne.matches(setTwo);
        assert setTwo.matches(setOne);

        diff = setOne.exclude(setTwo);
        assertThat(diff.size()).isEqualTo(0);

        diff = setOne.includeWrong(setTwo);
        assertThat(diff.size()).isEqualTo(0);

        diff = setTwo.includeWrong(setOne);
        assertThat(diff.size()).isEqualTo(0);

        diff = setOne.intersect(setTwo);
        assert setOne.matches(diff);
        assert diff.matches(setTwo);

        diff = setOne.notIntersect(setTwo);
        assertThat(diff.size()).isEqualTo(0);
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

            final Reader reader = new FileReader("boost-hashes.txt", StandardCharsets.UTF_8);
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

            final Reader reader = new FileReader("boost-hashes.txt", StandardCharsets.UTF_8);
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
