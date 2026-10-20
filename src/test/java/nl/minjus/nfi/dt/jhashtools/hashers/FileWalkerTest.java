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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * @author Erwin van Eijk <erwin.vaneijk@gmail.com>
 */
public class FileWalkerTest
{
    @TempDir
    public Path tempFolder;

    public FileWalkerTest()
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
     * Test of addWalkerVisitor method, of class FileWalker.
     */
    @Test
    public void testAddWalkerVisitor() {
        final WalkerVisitor visitor = null;
        final FileWalker instance = new FileWalker();
        instance.addWalkerVisitor(visitor);
        assertThat(instance.getWalkerVisitors().size()).isEqualTo(1);
        assertThat(instance.getWalkerVisitors().get(0)).isEqualTo(null);
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
        assertThat(result).isEqualTo(expResult);
    }

    static class WalkerVisitorImpl implements WalkerVisitor
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
        assertThat(result).isEqualTo(expResult);
        assertThat(visitor.getNumber()).isEqualTo(13);
    }

    /**
     * Test that symlinks to files are not followed.
     * This test is skipped if the filesystem does not support symbolic links.
     */
    @Test
    public void testWalkDoesNotFollowFileSymlinks() throws IOException {
        // Check if symlinks are supported on this filesystem
        try {
            final Path tempPath = tempFolder.resolve("symlink_test_temp");
            Files.createSymbolicLink(tempPath, tempPath.getParent());
            Files.delete(tempPath);
        } catch (UnsupportedOperationException | IOException e) {
            // Symlinks not supported, skip this test
            return;
        }

        // Create a test directory structure with a symlink
        final File testDir = Files.createDirectory(tempFolder.resolve("symlinkTest")).toFile();
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
        assertThat(visitor.getNumber()).as("Symlinks should not be followed, only real files visited").isEqualTo(1);
    }

    /**
     * Test that symlinks to directories are not followed.
     * This test is skipped if the filesystem does not support symbolic links.
     */
    @Test
    public void testWalkDoesNotFollowDirectorySymlinks() throws IOException {
        // Check if symlinks are supported on this filesystem
        try {
            final Path tempPath = tempFolder.resolve("symlink_test_temp");
            Files.createSymbolicLink(tempPath, tempPath.getParent());
            Files.delete(tempPath);
        } catch (UnsupportedOperationException | IOException e) {
            // Symlinks not supported, skip this test
            return;
        }

        // Create a test directory structure with a symlink to a directory
        final File testDir = Files.createDirectory(tempFolder.resolve("symlinkDirTest")).toFile();
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
        assertThat(visitor.getNumber()).as("Symlink directories should not be followed").isEqualTo(1);
    }

    /**
     * Test that regular files are still visited correctly.
     */
    @Test
    public void testWalkVisitsRegularFiles() throws IOException {
        final File testDir = Files.createDirectory(tempFolder.resolve("regularTest")).toFile();
        final File file1 = new File(testDir, "file1.txt");
        final File file2 = new File(testDir, "file2.txt");
        file1.createNewFile();
        file2.createNewFile();

        final FileWalker instance = new FileWalker();
        final WalkerVisitorImpl visitor = new WalkerVisitorImpl();
        instance.addWalkerVisitor(visitor);

        instance.walk(testDir);

        // Both files should be visited
        assertThat(visitor.getNumber()).as("Both regular files should be visited").isEqualTo(2);
    }
}