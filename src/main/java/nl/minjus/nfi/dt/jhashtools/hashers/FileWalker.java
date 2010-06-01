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

package nl.minjus.nfi.dt.jhashtools.hashers;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

/**
 * This class offers a 'walker' that will go through a directory and offer
 * the resulting files to one or more visitors.
 *
 * @author Erwin van Eijk
 */
class FileWalker {
    private FileFilter fileFilter;
    private final List<WalkerVisitor> visitors;
    private int visited;

    public FileWalker() {
        this.visitors = new LinkedList<WalkerVisitor>();
        this.fileFilter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return true;
            }
        };
    }

    public void setFileFilter(FileFilter filter) {
        this.fileFilter = filter;
    }

    public void addWalkerVisitor(WalkerVisitor visitor) {
        this.visitors.add(visitor);
    }

    public final List<WalkerVisitor> getWalkerVisitors() {
        return this.visitors;
    }

    public int walk(File file) {
        this.visited = 0;
        this.walkTheFile(file);
        return this.visited;
    }

    public int getVisited() {
        return this.visited;
    }

    private void walkTheFile(File file) {
        if (! file.exists()) {
            return;
        }
        if (file.isFile()) {
            fireVisitorsWith(file);
        } else if (file.isDirectory()) {
            for (File child: file.listFiles(this.fileFilter)) {
                if (child.isFile()) {
                    fireVisitorsWith(child);
                } else {
                    walkTheFile(child);
                }
            }
        }
    }

    private void fireVisitorsWith(File file) {
        for (WalkerVisitor visitor: this.visitors) {
            visitor.visit(file);
        }
        visited += 1;
    }
}
