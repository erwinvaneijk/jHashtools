/*
 * Copyright (c) 2010. Erwin van Eijk <erwin.vaneijk@gmail.com>
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
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools.hashers;

import org.junit.*;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author kojak
 */
public class FileWalkerTest {

    public FileWalkerTest() {
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
     * Test of addWalkerVisitor method, of class FileWalker.
     */
    @Test
    public void testAddWalkerVisitor() {
        WalkerVisitor visitor = null;
        FileWalker instance = new FileWalker();
        instance.addWalkerVisitor(visitor);
        assertEquals(1, instance.getWalkerVisitors().size());
        assertEquals(null, instance.getWalkerVisitors().get(0));
    }

    /**
     * Test of walk method, of class FileWalker.
     */
    @Test
    public void testWalkWithUnknownFile() {
        File file = new File("unknown");
        FileWalker instance = new FileWalker();
        int expResult = 0;
        int result = instance.walk(file);
        assertEquals(expResult, result);
    }

    class WalkerVisitorImpl implements WalkerVisitor {
        private int number;

        public WalkerVisitorImpl() {
            this.number = 0;
        }

        public final int getNumber() {
            return this.number;
        }

        public void visit(File file) {
            number += 1;
        }
    }

    /**
     * Test of walk method, of class FileWalker.
     */
    @Test
    public void testWalkWithKnownDirStructure() {
        File file = new File("testdata");
        FileWalker instance = new FileWalker();
        WalkerVisitorImpl visitor = new WalkerVisitorImpl();
        instance.addWalkerVisitor(visitor);
        int expResult = 13;
        int result = instance.walk(file);
        assertEquals(expResult, result);
        assertEquals(13, visitor.getNumber());
    }
}