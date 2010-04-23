/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

/**
 * This class offers a 'walker' that will go through a directory and offer
 * the resulting files to one or more visitors.
 * @author kojak
 */
public class FileWalker {
    private FileFilter fileFilter;
    private final List<WalkerVisitor> visitors;
    private boolean abort = false;
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
            for (WalkerVisitor visitor: this.visitors) {
                visitor.visit(file);
                visited += 1;
            }
        } else if (file.isDirectory()) {
            for (File child: file.listFiles(this.fileFilter)) {
                walkTheFile(child);
            }
        }
    }
}
