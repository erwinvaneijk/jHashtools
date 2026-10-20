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

package nl.minjus.nfi.dt.jhashtools.hashers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author eijk
 */
public class DirVisitorTest
{

    public DirVisitorTest()
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
     * Test of visit method, of class DirectoryVisitor.
     * 
     * @throws java.security.NoSuchAlgorithmException
     */
    @Test
    public void testVisit() throws NoSuchAlgorithmException {
        File theFile = new File("testdata/testfile1.bin");
        DirectoryVisitor instance = new DirectoryVisitor();
        assertThat(instance.isVerbose()).isFalse();
        instance.visit(theFile);
        assertThat(instance.getResults().size()).isEqualTo(1);
    }

    /**
     * Test of visit method, of class DirectoryVisitor.
     */
    @Test
    public void testVisitNonExistant() throws NoSuchAlgorithmException {
        try {
            File theFile = new File("does-not-exist");
            DirectoryVisitor instance = new DirectoryVisitor(DigestAlgorithmFactory.create("sha-256"));
            instance.visit(theFile);
            assertThat(instance.getResults().size()).isEqualTo(0);
        } catch (Throwable t) {
            fail("Nothing should be thrown here");
        }
    }

    /**
     * Test of visit method, of class DirectoryVisitor.
     */
    @Test
    public void testVisitNonExistantAlgorithm() {
        try {
            new DirectoryVisitor(DigestAlgorithmFactory.create("sha-345"));
            fail("We should have an NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException ex) {
            // pass
        }
    }

    /**
     * Test of setVerbose method, of class DirectoryVisitor.
     * 
     * @throws java.security.NoSuchAlgorithmException
     */
    @Test
    public void testSetVerbose() throws NoSuchAlgorithmException {
        boolean verbose = true;
        DirectoryVisitor instance = new DirectoryVisitor();
        instance.setVerbose(verbose);
        assertThat(instance.isVerbose()).isTrue();
    }

}