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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Erwin van Eijk <erwin.vaneijk@gmail.com>
 */
public class ConstructionInfoTest
{

    public ConstructionInfoTest()
    {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testSetup() {
        final ConstructionInfo info = new ConstructionInfo();
        assertThat(info.getConstructionDate()).isNotNull();
        assertThat(info.getOperatingSystem()).isNotNull();
        assertThat(info.getVersionInformation()).isNotNull();
        assertThat(info.getUsername()).isNotNull();
        requestDelay(10);
        assertThat(Calendar.getInstance().getTime().after(info.getConstructionDate())).isTrue();
    }

    @Test
    public void testSetters() {
        final ConstructionInfo info = new ConstructionInfo();
        info.setConstructionDate(Calendar.getInstance().getTime());

        info.setOperatingSystem("fooIS");
        assertThat(info.getOperatingSystem()).isEqualTo("fooIS");
        info.setVersionInformation("user");
        assertThat(info.getVersionInformation()).isEqualTo("user");
        info.setUsername("Username");
        assertThat(info.getUsername()).isEqualTo("Username");
        requestDelay(10);
        assertThat(Calendar.getInstance().getTime().after(info.getConstructionDate())).isTrue();
    }

    private void requestDelay(final int delayInMilisecs) {
        try {
            // insert a delay here. Otherwise, Date.after() will fail sometimes
            Thread.sleep(delayInMilisecs);
        } catch (final InterruptedException ex) {
            // We really do not care.
        }
    }

}
