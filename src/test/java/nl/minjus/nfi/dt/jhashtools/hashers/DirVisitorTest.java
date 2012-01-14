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

package nl.minjus.nfi.dt.jhashtools.hashers;

import org.junit.*;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 *
 * @author eijk
 */
public class DirVisitorTest {

    public DirVisitorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of visit method, of class DirectoryVisitor.
     * @throws java.security.NoSuchAlgorithmException
     */
    @Test
    public void testVisit() throws NoSuchAlgorithmException {
        File theFile = new File("testdata/testfile1.bin");
        DirectoryVisitor instance = new DirectoryVisitor();
        assertTrue( ! instance.isVerbose());
        instance.visit(theFile);
        assertEquals(1, instance.getResults().size());
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
            assertEquals(0, instance.getResults().size());
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
     * @throws java.security.NoSuchAlgorithmException
     */
    @Test
    public void testSetVerbose() throws NoSuchAlgorithmException {
        boolean verbose = true;
        DirectoryVisitor instance = new DirectoryVisitor();
        instance.setVerbose(verbose);
        assertTrue(instance.isVerbose());
    }

}