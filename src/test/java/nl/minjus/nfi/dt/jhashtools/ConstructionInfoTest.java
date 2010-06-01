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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 *
 * @author kojak
 */
public class ConstructionInfoTest {

    public ConstructionInfoTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSetup() {
        ConstructionInfo info = new ConstructionInfo();
        assertNotNull(info.getConstructionDate());
        assertNotNull(info.getOperatingSystem());
        assertNotNull(info.getVersionInformation());
        assertNotNull(info.getUsername());
        requestDelay(10);
        assertTrue(Calendar.getInstance().getTime().after(info.getConstructionDate()));
    }

    @Test
    public void testSetters() {
        ConstructionInfo info = new ConstructionInfo();
        info.setConstructionDate(Calendar.getInstance().getTime());

        info.setOperatingSystem("fooIS");
        assertEquals("fooIS", info.getOperatingSystem());
        info.setVersionInformation("user");
        assertEquals("user", info.getVersionInformation());
        info.setUsername("Username");
        assertEquals("Username", info.getUsername());
        requestDelay(10);
        assertTrue(Calendar.getInstance().getTime().after(info.getConstructionDate()));
    }

    private void requestDelay(int delayInMilisecs) {
        try {
            // insert a delay here. Otherwise, Date.after() will fail sometimes
            Thread.sleep(delayInMilisecs);
        } catch (InterruptedException ex) {
            // We really do not care.
        }
    }

}
