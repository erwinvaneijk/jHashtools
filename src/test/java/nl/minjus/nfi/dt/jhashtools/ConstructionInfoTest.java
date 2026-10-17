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
