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

package nl.minjus.nfi.dt.jhashtools.hashers;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import nl.minjus.nfi.dt.jhashtools.DigestOutputCreator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class offers a 'walker' that will go through a directory and offer the resulting files to one or more visitors.
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
        try {
            if (!aPath.exists()) {
                return;
            }
            if (aPath.isFile()) {
                fireVisitorsWith(aPath);
            } else if (aPath.isDirectory()) {
                for (final File child : aPath.listFiles()) {
                    if (child.isFile()) {
                        fireVisitorsWith(child);
                    } else {
                        walkTheFile(child);
                    }
                }
            }
        } catch (final InterruptedException e) {
            LOG.info("Execution interrupted");
        }
    }

    private void fireVisitorsWith(final File aFile) throws InterruptedException {
        for (final WalkerVisitor visitor : this.visitors) {
            visitor.visit(aFile);
        }
        visited += 1;
    }
}
