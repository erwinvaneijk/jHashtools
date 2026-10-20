/*
 * Copyright (c) 2012 Erwin van Eijk <erwin.vaneijk@gmail.com>.
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import nl.minjus.nfi.dt.jhashtools.hashers.ConcurrencyMode;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasherCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceStyle;

public class DigestOutputCreatorTest
{
    @Test
    public void testConstructor() {
        try {
            final DirectoryHasher directoryHasher = DirectoryHasherCreator.create(ConcurrencyMode.MULTI_THREADING);
            final ByteArrayOutputStream out = new ByteArrayOutputStream();

            final DigestOutputCreator creator = new DigestOutputCreator(out, directoryHasher, false);
            creator.generate(new String[] { "testdata" });

            final File testfile = new File("testfile-ignore.json");
            if (testfile.exists()) {
                testfile.delete();
            }
            assertThat(testfile.exists()).isFalse();
            creator.setOutputFile(testfile.getCanonicalPath());
            creator.setPersistenceStyle(PersistenceStyle.JSON);
            creator.finish();
            assertThat(testfile.exists()).isTrue();
            testfile.delete();

            assertThat(out.size()).isGreaterThan(100);

        } catch (final FileNotFoundException e) {
            fail("The file should have been created.");
        } catch (final IOException e) {
            fail("Should not happen");
        }

    }
}
