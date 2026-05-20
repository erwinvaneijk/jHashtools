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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author Erwin van Eijk <erwin.vaneijk@gmail.com>
 */
public class FileWalkerTest
{
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    public FileWalkerTest()
    {
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
        final WalkerVisitor visitor = null;
        final FileWalker instance = new FileWalker();
        instance.addWalkerVisitor(visitor);
        assertEquals(1, instance.getWalkerVisitors().size());
        assertEquals(null, instance.getWalkerVisitors().get(0));
    }

    /**
     * Test of walk method, of class FileWalker.
     */
    @Test
    public void testWalkWithUnknownFile() {
        final File file = new File("unknown");
        final FileWalker instance = new FileWalker();
        final int expResult = 0;
        final int result = instance.walk(file);
        assertEquals(expResult, result);
    }

    class WalkerVisitorImpl implements WalkerVisitor
    {
        private int number;

        public WalkerVisitorImpl()
        {
            this.number = 0;
        }

        public final int getNumber() {
            return this.number;
        }

        @Override
        public void visit(final File file) {
            number += 1;
        }
    }

    /**
     * Test of walk method, of class FileWalker.
     */
    @Test
    public void testWalkWithKnownDirStructure() {
        final File file = new File("testdata");
        final FileWalker instance = new FileWalker();
        final WalkerVisitorImpl visitor = new WalkerVisitorImpl();
        instance.addWalkerVisitor(visitor);
        final int expResult = 13;
        final int result = instance.walk(file);
        assertEquals(expResult, result);
        assertEquals(13, visitor.getNumber());
    }

    /**
     * Test that symlinks to files are not followed.
     * This test is skipped if the filesystem does not support symbolic links.
     */
    @Test
    public void testWalkDoesNotFollowFileSymlinks() throws IOException {
        // Check if symlinks are supported on this filesystem
        try {
            final Path tempPath = tempFolder.getRoot().toPath().resolve("symlink_test_temp");
            Files.createSymbolicLink(tempPath, tempPath.getParent());
            Files.delete(tempPath);
        } catch (UnsupportedOperationException | IOException e) {
            // Symlinks not supported, skip this test
            return;
        }
        
        // Create a test directory structure with a symlink
        final File testDir = tempFolder.newFolder("symlinkTest");
        final File realFile = new File(testDir, "realfile.txt");
        realFile.createNewFile();
        
        // Create a symlink to the real file
        final Path symlinkPath = testDir.toPath().resolve("symlink.txt");
        final Path targetPath = realFile.toPath();
        Files.createSymbolicLink(symlinkPath, targetPath);
        
        final FileWalker instance = new FileWalker();
        final WalkerVisitorImpl visitor = new WalkerVisitorImpl();
        instance.addWalkerVisitor(visitor);
        
        instance.walk(testDir);
        
        // Only the real file should be visited, not the symlink
        // The symlink itself is also a file, but it should be skipped
        // So we expect only 1 visit (the real file)
        assertEquals("Symlinks should not be followed, only real files visited", 1, visitor.getNumber());
    }

    /**
     * Test that symlinks to directories are not followed.
     * This test is skipped if the filesystem does not support symbolic links.
     */
    @Test
    public void testWalkDoesNotFollowDirectorySymlinks() throws IOException {
        // Check if symlinks are supported on this filesystem
        try {
            final Path tempPath = tempFolder.getRoot().toPath().resolve("symlink_test_temp");
            Files.createSymbolicLink(tempPath, tempPath.getParent());
            Files.delete(tempPath);
        } catch (UnsupportedOperationException | IOException e) {
            // Symlinks not supported, skip this test
            return;
        }
        
        // Create a test directory structure with a symlink to a directory
        final File testDir = tempFolder.newFolder("symlinkDirTest");
        final File realDir = new File(testDir, "realdir");
        realDir.mkdir();
        
        // Create a file in the real directory
        final File fileInRealDir = new File(realDir, "file.txt");
        fileInRealDir.createNewFile();
        
        // Create a symlink to the real directory
        final Path symlinkPath = testDir.toPath().resolve("linkdir");
        final Path targetPath = realDir.toPath();
        Files.createSymbolicLink(symlinkPath, targetPath);
        
        final FileWalker instance = new FileWalker();
        final WalkerVisitorImpl visitor = new WalkerVisitorImpl();
        instance.addWalkerVisitor(visitor);
        
        instance.walk(testDir);
        
        // Only the real directory and its file should be visited
        // The symlink directory should be skipped, and its contents should NOT be walked
        // So we expect only 1 visit (the file in the real directory)
        assertEquals("Symlink directories should not be followed", 1, visitor.getNumber());
    }

    /**
     * Test that regular files are still visited correctly.
     */
    @Test
    public void testWalkVisitsRegularFiles() throws IOException {
        final File testDir = tempFolder.newFolder("regularTest");
        final File file1 = new File(testDir, "file1.txt");
        final File file2 = new File(testDir, "file2.txt");
        file1.createNewFile();
        file2.createNewFile();
        
        final FileWalker instance = new FileWalker();
        final WalkerVisitorImpl visitor = new WalkerVisitorImpl();
        instance.addWalkerVisitor(visitor);
        
        instance.walk(testDir);
        
        // Both files should be visited
        assertEquals("Both regular files should be visited", 2, visitor.getNumber());
    }
}