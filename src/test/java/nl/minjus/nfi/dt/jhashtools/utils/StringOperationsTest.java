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

package nl.minjus.nfi.dt.jhashtools.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Erwin van Eijk <erwin.vaneijk@gmail.com>
 */
public class StringOperationsTest
{

    public StringOperationsTest()
    {
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of hexify method, of class StringOperations.
     */
    @Test
    public void testHexify() {
        final byte[] buf = new byte[] { 0x10, 0x11, 0x12, 0x13 };
        final String expResult = "10111213";
        final String result = StringOperations.hexify(buf);
        assertThat(result).isEqualTo(expResult);
    }

    @Test
    public void testNullCaseHexify() {
        assertThat(StringOperations.hexify(null)).isNull();
    }

    @Test
    public void testSplit() {
        final String str = "aaaabbbbccccdddd";
        final String result = StringOperations.split(str, 4);
        assertThat(result).isEqualTo("aaaa bbbb cccc dddd");
    }
}