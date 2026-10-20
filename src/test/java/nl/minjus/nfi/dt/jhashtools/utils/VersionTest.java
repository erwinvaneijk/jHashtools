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

package nl.minjus.nfi.dt.jhashtools.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class VersionTest
{

    @Test
    public void testGetVersion() {
        String version = Version.getVersion();
        // FIXME
        // The following test is WRONG. It should NOT return 1.2-unknown,
        // but since the versioninfo is derived from the Manifest.mf, that is
        // only
        // present in the jar-file, which is not there (yet) when this test is
        // run. Currently I have no way of overcoming that obstacle.
        assertThat(version).as("Version should be ").isEqualTo("1.2-unknown");
    }
}
