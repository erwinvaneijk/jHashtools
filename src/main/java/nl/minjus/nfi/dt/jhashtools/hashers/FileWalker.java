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

package nl.minjus.nfi.dt.jhashtools.hashers;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.minjus.nfi.dt.jhashtools.DigestOutputCreator;

/**
 * This class offers a 'walker' that will go through a directory and offer
 * the resulting files to one or more visitors.
 *
 * @author Erwin van Eijk
 */
class FileWalker
{
    private static final Logger LOG = LoggerFactory.getLogger(DigestOutputCreator.class);
    private final List<WalkerVisitor> visitors;
    private int visited;

    /**
     * Default constructor.
     */
    public FileWalker()
    {
        this.visitors = new LinkedList<WalkerVisitor>();
    }

    /**
     * Add a aVisitor to the list of visitors to signal when a file is found.
     *
     * @param aVisitor
     *            the aVisitor to add.
     */
    public void addWalkerVisitor(final WalkerVisitor aVisitor) {
        this.visitors.add(aVisitor);
    }

    /**
     * Get a list of all the visitors that are registered.
     *
     * @return a list.
     */
    public final List<WalkerVisitor> getWalkerVisitors() {
        return this.visitors;
    }

    /**
     * Starting at startPath, find all files that are 'under' there.
     *
     * @param aStartPath
     * @return
     */
    public int walk(final File aStartPath) {
        this.visited = 0;
        this.walkTheFile(aStartPath);
        return this.visited;
    }

    /**
     * Get the number of visited files.
     *
     * @return an int.
     */
    public int getVisited() {
        return this.visited;
    }

    private void walkTheFile(final File aPath) {
        if (!aPath.exists()) {
            return;
        }
        // Skip symbolic links
        if (isSymlink(aPath)) {
            LOG.debug("Skipping symlink: {}", aPath.getPath());
            return;
        }
        try {
            if (aPath.isFile()) {
                fireVisitorsWith(aPath);
            } else if (aPath.isDirectory()) {
                final File[] children = aPath.listFiles();
                if (children != null) {
                    for (final File child : children) {
                        walkTheFile(child);
                    }
                }
            }
        } catch (final InterruptedException e) {
            LOG.info("Execution interrupted");
        }
    }

    /**
     * Check if a file is a symbolic link.
     * Uses Files.isSymbolicLink() which checks the file itself,
     * not its parent paths.
     *
     * @param file
     *            the file to check
     * @return true if the file is a symbolic link
     */
    private boolean isSymlink(final File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        try {
            // Use Files.isSymbolicLink() to check the file itself
            // This avoids false positives from system-level symlinks
            // like /var -> /private/var
            return Files.isSymbolicLink(file.toPath());
        } catch (SecurityException e) {
            LOG.warn("Could not determine if file is symlink: {}", file, e);
            return false;
        }
    }

    private void fireVisitorsWith(final File aFile) throws InterruptedException {
        for (final WalkerVisitor visitor : this.visitors) {
            visitor.visit(aFile);
        }
        visited += 1;
    }
}
